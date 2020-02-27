package me.shkschneider

fun stringOf(vararg any: Any?) = buildString {
    any.forEach { append(it) }
}
