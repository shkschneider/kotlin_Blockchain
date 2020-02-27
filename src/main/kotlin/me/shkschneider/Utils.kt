package me.shkschneider

import java.time.Instant

// timestamp

val timestamp: Long get() = Instant.now().epochSecond

// Data

fun stringOf(vararg any: Any?) = buildString {
    any.forEach { append(it) }
}
