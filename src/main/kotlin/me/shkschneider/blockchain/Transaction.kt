package me.shkschneider.blockchain

import me.shkschneider.crypto.Coin
import me.shkschneider.crypto.Hashable
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.crypto.sign
import me.shkschneider.crypto.toCoin
import me.shkschneider.crypto.toHash
import me.shkschneider.crypto.verify
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class Transaction internal constructor(
    var inputs: MutableList<TransactionOutput> = mutableListOf(),
    var outputs: MutableList<TransactionOutput> = mutableListOf(),
    var signature: String? = null
) : Hashable() {

    val amount: Coin get() = inputs.toCoin()

    val fees: Coin get() = inputs.toCoin() - outputs.toCoin()

    val isCoinbase: Boolean get() = inputs.isEmpty()

    val isSigned: Boolean get() = signature != null

    fun sign(privateKey: PrivateKey) {
        signature = privateKey.sign(data)
    }

    fun verify(publicKey: PublicKey): Boolean =
        publicKey.verify(data, signature.orEmpty())

    override fun data(): String = stringOf(inputs, outputs, signature)

    override fun toString(): String = "Transaction {" + stringOf(
        " inputs=${inputs.size}",
        " outputs=${outputs.size}",
        " signature=${signature.toHash()}",
        " fees=[bit=${fees.bit},sat=${fees.sat}]",
        " amount=[bit=${amount.bit},sat=${amount.sat}]"
    ) + " }"

    companion object {

        fun coinbase(reward: Coin, coldWallet: ColdWallet): Transaction =
            Transaction(
                inputs = mutableListOf(),
                outputs = mutableListOf(TransactionOutput.coinbase(reward, coldWallet))
            ).apply { coldWallet.sign(this) }

    }

}
