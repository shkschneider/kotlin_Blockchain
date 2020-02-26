package me.shkschneider.crypto

object Hmac {

    fun sign(key: ByteArray, data: ByteArray): ByteArray =
        Crypto.hmac(key).doFinal(data)

    fun verify(key: ByteArray, data: ByteArray, expected: ByteArray): Boolean {
        sign(key, data).also {
            if (it.size != expected.size) return false
        }.forEachIndexed { i, b ->
            if (b.toInt().xor(expected[i].toInt()) != 0) return false
        }
        return true
    }

}
