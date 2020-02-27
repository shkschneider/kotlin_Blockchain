package me.shkschneider

import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.crypto.KeyPair
import me.shkschneider.data.Address
import me.shkschneider.data.Coin
import me.shkschneider.data.toHex
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal fun txo(amount: Coin = Consensus.reward(0)) = TransactionOutput(
    to = Address(Consensus.origin.public),
    amount = amount
)

class TransactionOutputTest {

    @Test
    fun `locked by default`() {
        txo().run {
            assertNotNull(lockScript)
            assertNull(unlockScript)
            assertFalse(isClaimed)
        }
    }

    @Test
    fun validate() {
        txo().validate()
    }

    // ALL txo should have positive amount

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid amount`() {
        txo(amount = Coin(-1)).validate()
    }

    // ALL claimed txo should have unlockScript matching lockScript

    @Test
    fun `valid unlockScript`() {
        txo().apply {
            unlock(Consensus.origin.private)
        }.validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid unlockScript (random key)`() {
        txo().apply {
            unlockScript = Random.nextBytes(256).toHex()
        }.validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid unlockScript (wrong key)`() {
        txo().apply {
            unlock(KeyPair.Factory().private)
        }.validate()
    }

}