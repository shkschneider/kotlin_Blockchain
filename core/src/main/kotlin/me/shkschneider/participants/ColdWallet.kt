package me.shkschneider.participants

import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.crypto.KeyPair
import me.shkschneider.data.Address
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin
import me.shkschneider.stringOf

open class ColdWallet(
    private val keyPair: KeyPair
) {

    fun unlock(tx: Transaction) {
        tx.unlock(keyPair.private)
    }

    fun sign(tx: Transaction) {
        tx.sign(keyPair.private)
    }

    fun address(): Address = Address(keyPair.public)

    protected fun outgoing(inputs: List<TransactionOutput>, to: Address, amount: Coin, fees: Coin): Transaction {
        return Transaction(inputs = inputs.toMutableList()).apply {
            val txo = TransactionOutput(to, amount)
            outputs.add(txo)
            val change: Coin = (inputs.toCoin() - amount) - fees
            if (change > 0) {
                outputs.add(TransactionOutput(address(), change))
            }
        }.apply {
            unlock(keyPair.private)
            sign(keyPair.private)
        }
    }

    override fun toString(): String = "ColdWallet {" + stringOf(
        " address=${address()}"
    ) + " }"

}
