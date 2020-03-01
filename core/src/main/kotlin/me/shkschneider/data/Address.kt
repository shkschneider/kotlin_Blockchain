package me.shkschneider.data

import me.shkschneider.crypto.PublicKey

data class Address(val publicKey: PublicKey) : Data() {

    override fun data(): ByteArray = publicKey.encoded

    override fun toString(): String = data.toHash().toByteArray().toBase58()

}
