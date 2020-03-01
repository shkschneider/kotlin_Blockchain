package me.shkschneider.data

import javax.xml.bind.DatatypeConverter

// lockScript unlockScript
typealias Hex = String

fun ByteArray.toHex(): String = DatatypeConverter.printHexBinary(this).toLowerCase()

fun Hex.fromHex(): ByteArray = DatatypeConverter.parseHexBinary(this)
