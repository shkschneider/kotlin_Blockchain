package me.shkschneider.crypto

import me.shkschneider.fromBase64
import me.shkschneider.toBase64

// Private

typealias PrivateKey = java.security.PrivateKey

fun PrivateKey.sign(msg: String): String =
    Crypto.signature().apply {
        initSign(this@sign)
        update(msg.toByteArray())
    }.run {
        sign().toBase64()
    }

// Public

typealias PublicKey = java.security.PublicKey

fun PublicKey.verify(msg: String, sig: String): Boolean =
    Crypto.signature().apply {
        initVerify(this@verify)
        update(msg.toByteArray())
    }.run {
        verify(sig.fromBase64())
    }
