package me.shkschneider

import me.shkschneider.blockchain.Transaction
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.crypto.KeyPair
import me.shkschneider.data.Coin
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun tx(coinbase: Boolean, signed: Boolean, claimed: Boolean) = Transaction(
    inputs = mutableListOf(),
    outputs = mutableListOf(txo(claimed = false))
).apply {
    if (coinbase && claimed) throw BlockchainException.TransactionException("coinbase claimed")
    if (!coinbase) inputs.add(txo(claimed = claimed))
    if (signed) sign(Consensus.origin.private)
}

class TransactionTest {

    @Test
    fun `validate coinbase`() {
        tx(coinbase = true, signed = true, claimed = false).apply {
            sign(Consensus.origin.private)
        }.validate()
    }

    @Test
    fun `validate non-coinbase`() {
        tx(coinbase = false, signed = true, claimed = true).apply {
            sign(Consensus.origin.private)
        }.validate()
    }

    // ALL tx should be signed

    @Test(expected = BlockchainException.TransactionException::class)
    fun `invalid signature _ not signed`() {
        tx(coinbase = false, signed = false, claimed = true).validate()
    }

    @Test(expected = BlockchainException.TransactionException::class)
    fun `invalid signature _ wrong privateKey`() {
        tx(coinbase = false, signed = false, claimed = true).apply {
            sign(KeyPair.Factory().private)
        }.validate()
    }

    @Test
    fun `coinbase has no inputs`() {
        tx(coinbase = true, signed = true, claimed = false).run {
            assertEquals(0, inputs.size)
        }
        tx(coinbase = false, signed = true, claimed = true).run {
            assertTrue { inputs.size > 0 }
        }
    }
    // ALL tx with fees should have more inputs than outputs

    @Test(expected = BlockchainException.TransactionException::class)
    fun `invalid amount`() {
        Transaction(
            inputs = mutableListOf(txo(claimed = true, amount = Coin(bit = 1.0))),
            outputs = mutableListOf(
                txo(claimed = false, amount = Coin(bit = 1.0)),
                txo(claimed = false, amount = Coin(bit = 1.0))
            )
        ).apply {
            sign(Consensus.origin.private)
        }.validate()
    }

    @Test
    fun `zero fees by default`() {
        assertEquals(0, tx(coinbase = true, signed = true, claimed = false).fees.sat)
        assertEquals(0, tx(coinbase = false, signed = true, claimed = true).fees.sat)
    }

    @Test(expected = BlockchainException.TransactionException::class)
    fun `invalid fees`() {
        Transaction(
            inputs = mutableListOf(txo(claimed = true, amount = Coin(bit = 0.9))),
            outputs = mutableListOf(txo(claimed = false, amount = Coin(bit = 1.0)))
        ).apply {
            sign(Consensus.origin.private)
        }.validate()
    }

    // ALL tx inputs should be claimed

    @Test(expected = BlockchainException.TransactionException::class)
    fun `invalid inputs _ claimed`() {
        tx(coinbase = false, signed = true, claimed = false).validate()
    }


}
