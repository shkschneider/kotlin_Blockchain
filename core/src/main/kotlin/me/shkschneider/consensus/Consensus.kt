package me.shkschneider.consensus

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Transaction
import me.shkschneider.crypto.KeyPair
import me.shkschneider.data.Coin
import me.shkschneider.participants.ColdWallet

object Consensus {

    object Algorithms {

        const val signature = "SHA1withRSA"
        const val hash = "SHA1"
        val keys = ("RSA" to 2048)
        const val random = "SHA1PRNG"
        const val hmac = "HmacSHA1"

    }

    private val versions = listOf(
        "0.1.3" to "e4f5471af51dec7fa3f5378649de825a7cf3b7bc",
        "0.1.2" to "48dc804af4a64a0fb46349beef10e94f4fef6a08",
        "0.1.1" to "a73777f588f6e474db8b32b1c13fd9f5f318c807",
        "0.1.0" to "fe753b23556364128df95d2ef135d87743e9d4a7"
    )
    val version: Pair<String, String> = versions.sortedByDescending { it.first }.first()

    object Rules {

        const val prefix: Char = '0'
        const val blockSize: Int = 10
        private const val halving: Int = 10

        fun reward(height: Int): Coin =
            Coin(bit = 1.0 / ((height / halving) + 1))

    }

    val origin: KeyPair = KeyPair.Factory("The Times 03/Jan/2009 Chancellor on brink of second bailout for banks")

    val genesis: Block = Block(
        height = 0,
        previous = null,
        difficulty = 1, // hard-coded
        nonce = 0
    ).apply {
        val coldWallet = ColdWallet(origin)
        add(
            Transaction.coinbase(
                Rules.reward(0),
                coldWallet
            ).also {
                coldWallet.sign(it)
            }
        )
    }

}