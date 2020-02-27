package me.shkschneider.crypto

import me.shkschneider.consensus.Consensus
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Crypto {

    private fun random(seed: String? = null) =
        SecureRandom.getInstance(Consensus.Algorithms.random).apply {
            seed?.let { setSeed(it.toByteArray()) }
        }

    @Throws(NoSuchAlgorithmException::class)
    fun signature(): Signature =
        Signature.getInstance(Consensus.Algorithms.signature)

    @Throws(NoSuchAlgorithmException::class)
    fun keyPairGenerator(seed: String? = null): KeyPairGenerator =
        KeyPairGenerator.getInstance(Consensus.Algorithms.keys.first).apply {
            initialize(
                Consensus.Algorithms.keys.second,
                random(seed)
            )
        }

    @Throws(NoSuchAlgorithmException::class)
    fun hmac(key: ByteArray): Mac =
        Mac.getInstance(Consensus.Algorithms.hmac).apply {
            init(SecretKeySpec(key, Consensus.Algorithms.hmac))
        }

    fun algorithms(): List<String> = mutableListOf<String>().apply {
        Security.getProviders().forEach { provider ->
            addAll(provider.services.filter { it.type == "Cipher" }.map { it.algorithm })
        }
    }

}
