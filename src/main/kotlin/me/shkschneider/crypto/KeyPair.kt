package me.shkschneider.crypto

import me.shkschneider.consensus.Consensus
import java.security.KeyPairGenerator
import java.security.SecureRandom

class KeyPair internal constructor(
    val private: PrivateKey,
    val public: PublicKey
) {

    companion object {

        @Suppress("FunctionName")
        fun Factory(seed: String? = null): KeyPair {
            val keyPair = KeyPairGenerator.getInstance(Consensus.algorithms.second.first).apply {
                initialize(
                    Consensus.algorithms.second.second,
                    SecureRandom.getInstance("${Consensus.algorithms.first}PRNG").apply {
                        seed?.let { setSeed(it.toByteArray()) }
                    }
                )
            }.generateKeyPair()
            return KeyPair(keyPair.private, keyPair.public)
        }

    }

    override fun toString(): String = throw SecurityException()

}
