package me.shkschneider.data

import java.time.Instant

typealias Timestamp = Long

val timestamp: Long get() = Instant.now().toEpochMilli()
