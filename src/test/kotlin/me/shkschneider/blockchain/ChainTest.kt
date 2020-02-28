package me.shkschneider.blockchain

import io.mockk.every
import io.mockk.mockk
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Coin
import org.junit.Test
import kotlin.test.assertEquals

class ChainTest {

    @Test
    fun validate() {
        Chain().validate()
    }

    @Test(expected = BlockchainException.ChainException::class)
    fun `amount is negative`() {
        with(mockk<Chain>(relaxed = true)) {
            every { amount } returns Coin(sat = -1)
            validate()
        }

    }

    // mempool

    @Test(expected = BlockchainException::class) // signing fails in Transaction.sing() before failing on Chain
    fun `mempool should be claimed tx`() {
        Chain().apply {
            mempool.add(tx(coinbase = false, signed = true, claimed = false))
        }.validate()
    }

    // blocks

    @Test(expected = BlockchainException.ChainException::class)
    fun `blocks are in disarray`() {
        Chain().apply {
            blocks.add(blk(height = 2).mine())
            blocks.add(blk(height = 1).mine())
        }.validate()
    }

    @Test(expected = BlockchainException.ChainException::class)
    fun `blocks should reference the previous hash`() {
        Chain().apply {
            blocks.add(blk(height = 1).copy(previous = blocks[0].hash).mine())
            blocks.add(blk(height = 2).copy(previous = blocks[0].hash).mine())
        }.validate()
    }

    // utxos

    @Test
    fun `unclaimed outputs from blocks should be in utxos`() {
        Chain().apply {
            assertEquals(utxos.size, blocks.flatMap { it.outputs }.filter { !it.isClaimed }.count())
        }
    }

    @Test(expected = BlockchainException.ChainException::class)
    fun `no txo should have been spent as input in a block`() {
        with(mockk<Chain>(relaxed = true)) {
            every { blocks } returns mutableListOf(Consensus.genesis.mine())
            every { utxos } returns blocks[0].outputs
            validate()
        }
    }

    // amount

    @Test(expected = BlockchainException.ChainException::class)
    fun `coins of the chain should be positive`() {
        with(mockk<Chain>(relaxed = true)) {
            every { amount } returns Coin(sat = -1)
            validate()
        }
    }

}
