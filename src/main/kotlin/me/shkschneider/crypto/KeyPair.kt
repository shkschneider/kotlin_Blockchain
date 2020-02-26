package me.shkschneider.crypto

class KeyPair internal constructor(
    val private: PrivateKey,
    val public: PublicKey
) {

    companion object {

        @Suppress("FunctionName")
        fun Factory(seed: String? = null): KeyPair {
            val keyPair = Crypto.keyPairGenerator(seed).generateKeyPair()
            return KeyPair(keyPair.private, keyPair.public)
        }

    }

    override fun toString(): String = throw SecurityException()

}
