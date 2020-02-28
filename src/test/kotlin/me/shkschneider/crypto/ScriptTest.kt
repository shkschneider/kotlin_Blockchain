package me.shkschneider.crypto

import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Address
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private const val times = 10

@RunWith(Parameterized::class)
class ScriptTest(pair: Pair<PrivateKey, TransactionOutput>) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = mutableListOf<Pair<PrivateKey, TransactionOutput>>().apply {
            repeat(times) {
                val keyPair = KeyPair.Factory()
                add(
                    keyPair.private to TransactionOutput(
                        to = Address(keyPair.public),
                        amount = Consensus.Rules.reward(0)
                    )
                )
            }
        }

    }

    private val key = pair.first
    private val txo = pair.second

    @Test
    fun unlock() {
        assertNotNull(txo.lockScript)
        txo.unlock(key).also {
            assertTrue { txo.unlockScript?.let { txo.validate() }?.run { true } ?: false }
        }
    }

}