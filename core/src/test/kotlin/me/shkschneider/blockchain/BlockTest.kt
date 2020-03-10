package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.participants.ColdMiner
import org.junit.Test

internal fun blk(height: Int) =
    if (height == 0) {
        Consensus.genesis
    } else {
        Block(
            height = height,
            previous = Consensus.genesis.hash,
            transactions = mutableListOf(
                tx(
                    coinbase = true,
                    signed = true,
                    claimed = false,
                    amount = Consensus.Rules.reward(height)
                )
            ),
            difficulty = Consensus.Rules.difficulty(height)
        )
    }

internal fun Block.mine(): Block = copy(nonce = ColdMiner().mine(this))

class BlockTest {

    @Test
    fun `validate (height=0)`() {
        blk(height = 0).mine().validate()
    }

    @Test
    fun `validate (height=1)`() {
        blk(height = 1).mine().validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block not mined`() {
        blk(height = 0).validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block badly mined`() {
        blk(height = 1)
            .copy(difficulty = 100, nonce = 1)
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block is orphan`() {
        blk(height = 1)
            .copy(previous = null)
            .mine()
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block has no coinbase tx`() {
        blk(height = 1)
            .copy(transactions = mutableListOf())
            .mine()
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block is too big`() {
        blk(height = 1).apply {
            for (i in 1 until Consensus.Rules.blockSize + 1) {
                transactions.add(
                    tx(
                        coinbase = false,
                        signed = true,
                        claimed = true
                    ).apply {
                        outputs.add(txo(claimed = false))
                    })
            }
        }.mine().validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block has bad reward`() {
        blk(height = 1)
            .copy(transactions = mutableListOf(
                tx(coinbase = true, signed = true, claimed = false).apply {
                    outputs = mutableListOf(outputs.first().copy(amount = Consensus.Rules.reward(0) * 2))
                }
            ))
            .mine()
            .validate()
    }

}
