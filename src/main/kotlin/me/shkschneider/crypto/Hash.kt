package me.shkschneider.crypto

import me.shkschneider.consensus.Consensus
import java.security.Key
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

fun String?.toHash(): Hash = orEmpty().toByteArray().toHash()

fun Key.toHash() = toString().toHash()

private fun ByteArray.toHex(): String = DatatypeConverter.printHexBinary(this)

private fun ByteArray.toHash(): Hash = MessageDigest.getInstance(Consensus.Algorithms.hash)
    .digest(this)
    .toHex()
    .toLowerCase()

typealias Hash = String

val Hash.difficulty: Int get() = takeWhile { it == Consensus.Rules.prefix }.count()

abstract class Hashable {

    val hash: Hash get() = data.toHash()

    val data: String get() = data()

    protected abstract fun data(): String

    override fun hashCode(): Int = data.hashCode()

    override fun equals(other: Any?): Boolean = this.hashCode() == other.hashCode()

}
