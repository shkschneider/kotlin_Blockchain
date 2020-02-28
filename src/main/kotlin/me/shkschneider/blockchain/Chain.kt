package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin
import me.shkschneider.participants.ColdMiner
import me.shkschneider.print
import me.shkschneider.stringOf

class Chain {

    val blocks: MutableList<Block> = mutableListOf()
    val mempool: MutableList<Transaction> = mutableListOf()
    val height: Int get() = blocks.size
    val work: Int get() = blocks.sumBy { it.difficulty }
    var difficulty: Int = 1 // hard-coded

    init {
        println("v${Consensus.version.first} (${Consensus.version.second})")
        Consensus.genesis.copy(nonce = ColdMiner().mine(Consensus.genesis)).let { genesis ->
            genesis.validate()
            add(genesis)
        }
        validate()
    }

    val genesis: Block get() = blocks.first().takeIf { it.height == 0 } ?: throw BlockchainException.ChainException("genesis")

    val utxos: List<TransactionOutput>
        get() =
            // each output
            blocks.flatMap { it.outputs }.filterNot { txo ->
                // that is not spent as an input
                blocks.flatMap { it.inputs }.any { it == txo }
            }

    val amount: Coin get() = utxos.toCoin()

    fun estimatedSupply(): Coin {
        var height = 0
        var coins = Consensus.reward(height)
        while (true) {
            val reward = Consensus.reward(++height)
            if (reward.sat <= 1) break
            coins += reward
        }
        return coins
    }

    fun add(tx: Transaction) {
        tx.validate()
        if (tx.inputs.any { txo ->
                blocks.flatMap { it.inputs }.contains(txo)
            }) throw BlockchainException.ChainException("double-spend")
        if (!mempool.add(tx)) throw BlockchainException.ChainException("mempool")
    }

    fun add(block: Block) {
        block.validate()
        mempool.removeAll(block.transactions)
        if (!blocks.add(block)) throw BlockchainException.ChainException("block")
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
