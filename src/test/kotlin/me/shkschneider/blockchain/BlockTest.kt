package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.participants.ColdMiner
import org.junit.Test

internal fun blk(genesis: Boolean) =
    if (genesis) {
        Consensus.genesis
    } else {
        Block(
            height = 1,
            previous = Consensus.genesis.hash,
            transactions = mutableListOf(
                tx(
                    coinbase = true,
                    signed = true,
                    claimed = false
                )
            ),
            difficulty = Consensus.genesis.difficulty
        )
    }

internal fun Block.mine(): Block = copy(nonce = ColdMiner().mine(this))

class BlockTest {

    @Test
    fun `validate (height=0)`() {
        blk(genesis = true).mine().validate()
    }

    @Test
    fun `validate (height=1)`() {
        blk(genesis = false).mine().validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block not mined`() {
        blk(genesis = true).validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block badly mined`() {
        blk(genesis = false)
            .copy(difficulty = 100, nonce = 1)
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block is orphan`() {
        blk(genesis = false)
            .copy(previous = null)
            .mine()
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block has no coinbase tx`() {
        blk(genesis = false)
            .copy(transactions = mutableListOf())
            .mine()
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block has bad reward`() {
        blk(genesis = false)
            .copy(transactions = mutableListOf(
                tx(coinbase = true, signed = true, claimed = false).apply {
                    outputs = mutableListOf(outputs.first().copy(amount = Consensus.reward(0) * 2))
                }
            ))
            .mine()
            .validate()
    }

    @Test(expected = BlockchainException.BlockException::class)
    fun `block is too big`() {
        blk(genesis = false).apply {
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

}
