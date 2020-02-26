package me.shkschneider.crypto

// Private

typealias PrivateKey = java.security.PrivateKey

fun PrivateKey.sign(msg: ByteArray): ByteArray =
    Crypto.signature().apply {
        initSign(this@sign)
        update(msg)
    }.run {
        sign()
    }

// Public

typealias PublicKey = java.security.PublicKey

fun PublicKey.verify(msg: ByteArray, signature: ByteArray): Boolean =
    Crypto.signature().apply {
        initVerify(this@verify)
        update(msg)
    }.run {
        verify(signature)
    }
