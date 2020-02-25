package me.shkschneider.participants

import me.shkschneider.crypto.Coin
import me.shkschneider.blockchain.Block
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.blockchain.Chain
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.PublicKey
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.validate
import me.shkschneider.blockchain.Transaction
import me.shkschneider.crypto.KeyPair
import me.shkschneider.crypto.toCoin
import me.shkschneider.crypto.toHash
import me.shkschneider.stringOf

class HotWallet(
    private val chain: Chain,
    private: PrivateKey,
    private val public: PublicKey
) : ColdWallet(private, public) {

    companion object {

        @Suppress("FunctionName")
        fun Factory(chain: Chain, seed: String? = null) = with(KeyPair.Factory(seed)) {
            HotWallet(chain, private, public)
        }

    }

    private val mempool: List<Transaction> get() = chain.mempool
    private val utxos: List<TransactionOutput> get() = chain.utxos.toMutableList()

    fun balance(): Coin =
        chain.utxos.filter { it.to == public }.toCoin()

    fun send(to: PublicKey, amount: Coin, fees: Coin = Coin(sat = 1)) {
        if (balance() < amount + fees) throw BlockchainException("balance")
        val inputs = mutableListOf<TransactionOutput>()
        chain.utxos.filter { it.to == public }.sortedBy { it.amount }.forEach { utxo ->
            inputs += utxo
            if (inputs.toCoin() >= amount + fees) {
                return@forEach
            }
        }
        val tx = outgoing(inputs, to, amount, fees)
        send(tx)
    }

    fun send(tx: Transaction) {
        if (!tx.isSigned) {
            sign(tx)
        }
        tx.validate()
        chain.add(tx)
    }

    fun send(blk: Block) {
        blk.validate()
        chain.add(blk)
    }

    override fun toString(): String = "HotWallet {" + stringOf(
        " address=${address().toHash()}",
        " balance=[bit=${balance().bit},sat=${balance().sat}]"
    ) + " }"

}
