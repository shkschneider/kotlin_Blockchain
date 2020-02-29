package me.shkschneider.crypto

import java.security.SignatureException

// Private

typealias PrivateKey = java.security.PrivateKey

@Throws(SignatureException::class)
internal fun PrivateKey.sign(data: ByteArray): ByteArray =
    Crypto.signature().apply {
        initSign(this@sign)
        update(data)
    }.run {
        sign()
    }

// Public

typealias PublicKey = java.security.PublicKey

@Throws(SignatureException::class)
internal fun PublicKey.verify(data: ByteArray, signature: ByteArray): Boolean =
    Crypto.signature().apply {
        initVerify(this@verify)
        update(data)
    }.run {
        verify(signature)
    }
