package me.shkschneider.blockchain

import io.mockk.every
import io.mockk.mockk
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

internal fun txo(claimed: Boolean, amount: Coin = Consensus.Rules.reward(0)) = TransactionOutput(
    to = Address(Consensus.origin.public),
    amount = amount
).apply {
    if (claimed) unlock(Consensus.origin.private)
}

class TransactionOutputTest {

    @Test
    fun `locked by default`() {
        txo(claimed = false).run {
            assertNotNull(lockScript)
            assertNull(unlockScript)
            assertFalse(isClaimed)
        }
    }

    @Test
    fun validate() {
        txo(claimed = false).validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `negative amount`() {
        txo(claimed = false, amount = Coin(-1)).validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid lockScript`() {
        val txo = txo(claimed = false, amount = Consensus.Rules.reward(0))
        with(mockk<TransactionOutput>()) {
            every { to } returns txo.to
            every { amount } returns txo.amount
            every { lockScript } returns Random.nextBytes(256).toHex()
            every { data } returns txo.data
            validate()
        }
    }

    @Test
    fun `valid unlockScript`() {
        txo(claimed = false).apply {
            unlock(Consensus.origin.private)
        }.validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid unlockScript (random key)`() {
        txo(claimed = false).apply {
            unlockScript = Random.nextBytes(256).toHex()
        }.validate()
    }

    @Test(expected = BlockchainException.TransactionOutputException::class)
    fun `invalid unlockScript (wrong key)`() {
        txo(claimed = false).apply {
            unlock(KeyPair.Factory().private)
        }.validate()
    }

}
