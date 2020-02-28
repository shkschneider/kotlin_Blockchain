package me.shkschneider

fun <T> List<T>.unique(predicate: (T) -> Any): Boolean =
    distinctBy { predicate(it) }.size == this.size

fun stringOf(vararg any: Any?) = buildString {
    any.forEach { append(it) }
}
