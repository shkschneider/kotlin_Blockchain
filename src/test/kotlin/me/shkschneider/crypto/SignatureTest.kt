package me.shkschneider.crypto

import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.tx
import me.shkschneider.data.fromBase64
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertTrue

private const val times = 10

@RunWith(Parameterized::class)
class SignatureTest(pair: Pair<PublicKey, Transaction>) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = mutableListOf<Pair<PublicKey, Transaction>>().apply {
            repeat(times) {
                val keyPair = KeyPair.Factory()
                add(
                    keyPair.public to tx(
                        coinbase = false,
                        signed = false,
                        claimed = true
                    ).apply {
                        sign(keyPair.private)
                    }
                )
            }
        }

    }

    private val key = pair.first
    private val tx = pair.second

    @Test
    fun signature() {
        assertTrue { tx.signature?.let { key.verify(tx.data, it.fromBase64()) } ?: false }
    }

}