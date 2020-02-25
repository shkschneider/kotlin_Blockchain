package me.shkschneider.participants

import me.shkschneider.crypto.Coin
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.crypto.sign
import me.shkschneider.crypto.verify
import me.shkschneider.blockchain.Transaction
import me.shkschneider.crypto.toCoin
import me.shkschneider.crypto.toHash
import me.shkschneider.stringOf

open class ColdWallet(
    private val private: PrivateKey,
    private val public: PublicKey
) {

    fun sign(tx: Transaction): Transaction = tx.apply { sign(private) }

    fun sign(msg: String) = private.sign(msg)

    fun verify(msg: String, signature: String) = public.verify(msg, signature)

    fun address() = public

    fun outgoing(inputs: MutableList<TransactionOutput>, to: PublicKey, amount: Coin) =
        Transaction(inputs = inputs).apply {
            val txo = TransactionOutput(to, amount)
            outputs.add(txo)
            val change: Coin = (inputs.toCoin() - amount)
            if (change > 0) {
                outputs.add(TransactionOutput(public, change))
            }
        }.apply { sign(private) }

    override fun toString(): String = "ColdWallet {" + stringOf(
        " address=${address().toHash()}"
    ) + " }"

}
