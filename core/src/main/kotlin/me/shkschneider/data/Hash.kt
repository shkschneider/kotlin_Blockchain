package me.shkschneider.data

import me.shkschneider.consensus.Consensus
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

private fun ByteArray.toHexadecimal(): String = DatatypeConverter.printHexBinary(this)

fun ByteArray.toHash(): Hash = MessageDigest.getInstance(Consensus.Algorithms.hash)
    .digest(this)
    .toHexadecimal()
    .toLowerCase()

// blocks
typealias Hash = String

val Hash.difficulty: Int get() = takeWhile { it == Consensus.Rules.prefix }.count()
