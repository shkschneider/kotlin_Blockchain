package me.shkschneider.data

import java.time.Instant

typealias Timestamp = Long

val timestamp get() = Instant.now().epochSecond
