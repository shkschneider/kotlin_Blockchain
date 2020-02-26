package me.shkschneider.blockchain

import me.shkschneider.data.Coin
import me.shkschneider.data.Data
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.crypto.sign
import me.shkschneider.data.toCoin
import me.shkschneider.crypto.verify
import me.shkschneider.data.Base64
import me.shkschneider.data.toBase64
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class Transaction internal constructor(
    var inputs: MutableList<TransactionOutput> = mutableListOf(),
    var outputs: MutableList<TransactionOutput> = mutableListOf(),
    var signature: Base64? = null
) : Data() {

    val amount: Coin get() = inputs.toCoin()

    val fees: Coin get() = inputs.toCoin() - outputs.toCoin()

    val isCoinbase: Boolean get() = inputs.isEmpty()

    val isSigned: Boolean get() = signature != null

    fun sign(privateKey: PrivateKey) {
        signature = privateKey.sign(data).toBase64()
    }

    fun verify(publicKey: PublicKey): Boolean =
        publicKey.verify(data, signature.orEmpty().toByteArray())

    override fun data(): ByteArray = stringOf(inputs, outputs, signature).toByteArray()

    override fun toString(): String = "Transaction {" + stringOf(
        " inputs=${inputs.size}",
        " outputs=${outputs.size}",
        " signature=$signature",
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
