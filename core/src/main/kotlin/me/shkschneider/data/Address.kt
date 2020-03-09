package me.shkschneider.data

import me.shkschneider.crypto.PublicKey

data class Address(val publicKey: PublicKey) {

    override fun toString(): String = publicKey.encoded.toHash().toByteArray().toBase58()

}
