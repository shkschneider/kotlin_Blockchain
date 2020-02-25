package me.shkschneider

import java.time.Instant
import java.util.Base64

// timestamp

val timestamp: Long get() = Instant.now().epochSecond

// String

fun stringOf(vararg any: Any?) = buildString {
    any.forEach { append(it) }
}

// Base64

fun ByteArray.toBase64(): String =
    Base64.getEncoder().encodeToString(this)

fun String.fromBase64(): ByteArray =
    Base64.getDecoder().decode(this)
