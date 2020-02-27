package me.shkschneider.blockchain

import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.crypto.sign
import me.shkschneider.crypto.verify
import me.shkschneider.data.Base64
import me.shkschneider.data.Coin
import me.shkschneider.data.Data
import me.shkschneider.data.fromBase64
import me.shkschneider.data.toBase64
import me.shkschneider.data.toCoin
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class Transaction(
    var inputs: MutableList<TransactionOutput> = mutableListOf(),
    var outputs: MutableList<TransactionOutput> = mutableListOf(),
    var signature: Base64? = null
) : Data() {

    val isCoinbase: Boolean get() = inputs.isEmpty()

    val amount: Coin get() = inputs.toCoin()
    val fees: Coin get() = inputs.toCoin() - outputs.toCoin()

    val isSigned: Boolean get() = signature != null

    fun unlock(privateKey: PrivateKey) {
        inputs.forEach { it.unlock(privateKey) }
    }

    fun sign(privateKey: PrivateKey) {
        signature = privateKey.sign(data).toBase64()
    }

    fun verify(publicKey: PublicKey): Boolean =
        publicKey.verify(data, signature.orEmpty().fromBase64())

    override fun data(): ByteArray = stringOf(
        time,
        inputs.map { it.data.toBase64() },
        outputs.map { it.data.toBase64() }
    ).toByteArray()

    override fun toString(): String = "Transaction {" + stringOf(
        " time=$time",
        " inputs=${inputs.size}",
        " outputs=${outputs.size}",
        " isSigned=$isSigned",
        " fees=[bit=${fees.bit},sat=${fees.sat}]",
        " amount=[bit=${amount.bit},sat=${amount.sat}]"
    ) + " }"

    companion object {

        fun coinbase(reward: Coin, coldWallet: ColdWallet): Transaction =
            Transaction(
                inputs = mutableListOf(),
                outputs = mutableListOf(TransactionOutput.coinbase(reward, coldWallet))
            ).apply {
                coldWallet.sign(this)
            }

    }

}
