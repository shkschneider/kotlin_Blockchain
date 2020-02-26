package me.shkschneider.crypto

class KeyPair internal constructor(
    val private: PrivateKey,
    val public: PublicKey
) {

    companion object {

        @Suppress("FunctionName")
        fun Factory(seed: String? = null): KeyPair = with(Crypto.keyPairGenerator(seed).generateKeyPair()) {
            KeyPair(private, public)
        }

    }

    override fun toString(): String = throw SecurityException()

}
