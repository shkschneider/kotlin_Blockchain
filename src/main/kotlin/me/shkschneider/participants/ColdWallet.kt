package me.shkschneider.participants

import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.crypto.sign
import me.shkschneider.crypto.verify
import me.shkschneider.data.Address
import me.shkschneider.data.Base64
import me.shkschneider.data.Coin
import me.shkschneider.data.fromBase64
import me.shkschneider.data.toBase64
import me.shkschneider.data.toCoin
import me.shkschneider.stringOf

open class ColdWallet(
    protected val private: PrivateKey,
    private val public: PublicKey
) {

    fun unlock(tx: Transaction) = with(tx) {
        unlock(private)
    }

    fun sign(tx: Transaction) = with(tx) {
        sign(private)
    }

    fun sign(msg: String): Base64 = private.sign(msg.toByteArray()).toBase64()

    fun verify(msg: String, signature: Base64) = public.verify(msg.toByteArray(), signature.fromBase64())

    fun address(): Address = Address(public)

    protected fun outgoing(inputs: MutableList<TransactionOutput>, to: Address, amount: Coin, fees: Coin): Transaction {
        return Transaction(inputs = inputs).apply {
            val txo = TransactionOutput(to, amount)
            outputs.add(txo)
            val change: Coin = (inputs.toCoin() - amount) - fees
            if (change > 0) {
                outputs.add(TransactionOutput(address(), change))
            }
        }.apply {
            unlock(private)
            sign(private)
        }
    }

    override fun toString(): String = "ColdWallet {" + stringOf(
        " address=${address()}"
    ) + " }"

}
