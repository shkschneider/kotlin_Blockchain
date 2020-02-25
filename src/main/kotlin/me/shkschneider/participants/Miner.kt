package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Transaction
import me.shkschneider.consensus.Consensus
import me.shkschneider.crypto.difficulty
import kotlin.random.Random

open class Miner {

    fun unstack(mempool: List<Transaction>): List<Transaction> =
        with(mutableListOf<Transaction>()) {
            mempool.forEach { tx ->
                // double-spend: none of inputs should already be added
                if (tx.inputs.none { txo -> flatMap { it.inputs }.contains(txo) }) {
                    this += tx
                }
                if (size == Consensus.blockSize - 1) return@with this
            }
            return this
        }

    fun mine(blk: Block): Long {
        while (true) {
            val nonce = Random.nextLong()
            if (blk.copy(nonce = nonce).hash.difficulty >= blk.difficulty)
                return nonce
        }
    }

}
