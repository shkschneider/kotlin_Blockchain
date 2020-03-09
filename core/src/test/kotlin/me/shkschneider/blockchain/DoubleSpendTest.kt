package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import org.junit.Test

class DoubleSpendTest {

    @Test(expected = BlockchainException::class)
    fun `double-spend txo in tx inputs`() {
        tx(coinbase = false, claimed = true, signed = false).apply {
            val txo = txo(claimed = true)
            inputs = mutableListOf(txo, txo)
            sign(Consensus.origin.private)
        }.validate()
    }

    @Test(expected = BlockchainException::class)
    fun `double-spend txo in blk txs`() {
        val txo = txo(claimed = true)
        blk(height = 1)
            .copy(transactions = mutableListOf(
                tx(coinbase = true, signed = true, claimed = false),
                tx(coinbase = false, signed = false, claimed = true).copy(inputs = mutableListOf(txo)).apply {
                    sign(Consensus.origin.private)
                },
                tx(coinbase = false, signed = false, claimed = true).copy(inputs = mutableListOf(txo, txo)).apply {
                    sign(Consensus.origin.private)
                }
            ))
            .mine()
            .validate()
    }

    @Test
    fun `double-spend in utxos`() {
        // as utxos are extracted from blocks', it cannot be cheated
    }

}
