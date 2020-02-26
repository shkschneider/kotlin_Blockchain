package me.shkschneider

import java.time.Instant
import java.util.Base64

// timestamp

val timestamp: Long get() = Instant.now().epochSecond

// String

fun stringOf(vararg any: Any?) = buildString {
    any.forEach { append(it) }
}
