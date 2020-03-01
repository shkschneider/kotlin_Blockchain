package me.shkschneider.data

typealias Base64 = String

fun ByteArray.toBase64(): Base64 =
    java.util.Base64.getEncoder().encodeToString(this)

fun Base64.fromBase64(): ByteArray =
    java.util.Base64.getDecoder().decode(this)
