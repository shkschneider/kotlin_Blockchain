package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.crypto.Coin
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.crypto.toCoin
import me.shkschneider.participants.ColdMiner
import me.shkschneider.participants.HotMiner
import me.shkschneider.stringOf

class Chain {

    val blocks: MutableList<Block> = mutableListOf()
    val mempool: MutableList<Transaction> = mutableListOf()
    val height: Int get() = blocks.size
    val work: Int get() = blocks.sumBy { it.difficulty }
    var difficulty: Int = 1 // hard-coded

    init {
        println("version=${Consensus.version}")
        Consensus.genesis.copy(nonce = ColdMiner().mine(Consensus.genesis)).let { genesis ->
            println("block[${genesis.height}]=${genesis.hash}")
            genesis.validate()
            add(genesis)
        }
        validate()
    }

    val utxos: List<TransactionOutput>
        get() =
            // each output
            blocks.flatMap { it.outputs }.filterNot { txo ->
                // that is not spent as an input
                blocks.flatMap { it.inputs }.any { it == txo }
            }

    val amount: Coin get() = utxos.toCoin()

    fun add(tx: Transaction) {
        tx.validate()
        if (tx.inputs.any { txo ->
            blocks.flatMap { it.inputs }.contains(txo)
        }) throw BlockchainException("double-spend")
        if (!mempool.add(tx)) throw BlockchainException("mempool")
    }

    fun add(block: Block) {
        block.validate()
        mempool.removeAll(block.transactions)
        if (!blocks.add(block)) throw BlockchainException("block")
    }

    override fun toString(): String = "Chain {" + stringOf(
        " blocks=${blocks.size}",
        " mempool=${mempool.size}",
        " utxos=${utxos.size}",
        " work=$work",
        " difficulty=$difficulty",
        " amount=[bit=${amount.bit},sat=${amount.sat}]"
    ) + " }"

}
