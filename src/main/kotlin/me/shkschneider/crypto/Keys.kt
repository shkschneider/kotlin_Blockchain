package me.shkschneider.crypto

import me.shkschneider.consensus.Consensus
import me.shkschneider.fromBase64
import me.shkschneider.toBase64
import java.security.Key
import java.security.SecureRandom
import java.security.Signature

fun Key.toHash() = toString().toHash()

private val signature: Signature = Signature.getInstance("${Consensus.algorithms.first}with${Consensus.algorithms.second.first}")

// Private

typealias PrivateKey = java.security.PrivateKey

fun PrivateKey.sign(msg: String): String =
    signature.apply {
        initSign(this@sign)
        update(msg.toByteArray())
    }.run {
        sign().toBase64()
    }

// Public

typealias PublicKey = java.security.PublicKey

fun PublicKey.verify(msg: String, sig: String): Boolean =
    signature.apply {
        initVerify(this@verify)
        update(msg.toByteArray())
    }.run {
        verify(sig.fromBase64())
    }
