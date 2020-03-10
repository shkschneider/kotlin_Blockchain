package me.shkschneider.blockchain

import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin
import me.shkschneider.participants.ColdMiner
import me.shkschneider.stringOf

class Chain {

    val blocks: MutableList<Block> = mutableListOf()
    val mempool: MutableList<Transaction> = mutableListOf()
    val height: Int get() = blocks.size
    val work: Int get() = blocks.sumBy { it.difficulty }
    val difficulty: Int get() = Consensus.Rules.difficulty(height)

    init {
        with(Consensus.genesis) {
            this.copy(nonce = ColdMiner().mine(this)).run {
                validate()
                add(this)
            }
        }
        validate()
    }

    val utxos: List<TransactionOutput>
        get() = blocks.flatMap { it.outputs }.filter { !it.isClaimed }

    val amount: Coin get() = utxos.toCoin()

    fun estimatedSupply(): Coin {
        var height = 0
        var coins = Consensus.Rules.reward(height)
        while (true) {
            val reward = Consensus.Rules.reward(++height)
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

    fun add(blk: Block) {
        blk.validate()
        mempool.removeAll(blk.transactions)
        if (!blocks.add(blk)) throw BlockchainException.ChainException("block")
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
