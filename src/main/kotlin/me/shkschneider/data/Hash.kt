package me.shkschneider.data

import me.shkschneider.consensus.Consensus
import java.security.Key
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

fun String?.toHash(): Hash = orEmpty().toByteArray().toHash()

fun Key.toHash() = encoded.toHash()

private fun ByteArray.toHexadecimal(): String = DatatypeConverter.printHexBinary(this)

fun ByteArray.toHash(): Hash = MessageDigest.getInstance(Consensus.Algorithms.hash)
    .digest(this)
    .toHexadecimal()
    .toLowerCase()

typealias Hash = String

val Hash.difficulty: Int get() = takeWhile { it == Consensus.Rules.prefix }.count()
