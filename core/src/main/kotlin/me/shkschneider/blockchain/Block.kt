package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Coin
import me.shkschneider.data.Hash
import me.shkschneider.data.Timestamp
import me.shkschneider.data.timestamp
import me.shkschneider.data.toBase64
import me.shkschneider.data.toHash
import me.shkschneider.stringOf

data class Block(
    val height: Int,
    val previous: Hash?,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val difficulty: Int = Consensus.genesis.difficulty,
    val time: Timestamp = timestamp,
    var nonce: Long = 0 // proof-of-work
) {

    val hash: Hash get() = data.toHash()

    val isMined: Boolean get() = nonce != 0.toLong()
    val isGenesis: Boolean get() = height == 0
    val coinbase: Transaction
        get() = transactions.first().takeIf { it.isCoinbase } ?: throw BlockchainException.BlockException("coinbase")

    val inputs: List<TransactionOutput> get() = transactions.flatMap { it.inputs }
    val outputs: List<TransactionOutput> get() = transactions.flatMap { it.outputs }
    val fees: Coin get() = Coin(sat = transactions.sumBy { it.fees.sat })
    val size: Int get() = transactions.size

    fun add(tx: Transaction): Boolean {
        tx.validate()
        return transactions.add(tx)
    }

    val data: ByteArray get() = stringOf(
        time,
        height,
        previous,
        transactions.map { it.data.toBase64() },
        nonce
    ).toByteArray()

    override fun toString(): String = "Block {" + stringOf(
        " time=$time",
        " height=$height",
        " hash=$hash",
        " previous=$previous",
        " height=$height",
        " transactions=${transactions.size}",
        " difficulty=$difficulty",
        " nonce=$nonce"
    ) + " }"

}
