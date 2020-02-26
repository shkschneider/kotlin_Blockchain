package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.consensus.Consensus
import me.shkschneider.crypto.fees

open class HotMiner(
    private val chain: Chain,
    private val coldWallet: ColdWallet
) : ColdMiner() {

    private fun unstack(): List<Transaction> =
        with(mutableListOf<Transaction>()) {
            chain.mempool.forEach { tx ->
                // double-spend: none of inputs should already be added
                if (tx.inputs.none { txo -> flatMap { it.inputs }.contains(txo) }) {
                    this += tx
                }
                if (size == Consensus.Rules.blockSize - 1) return@with this
            }
            return this
        }

    fun mine(): Block {
        val txs = unstack()
        val blk = Block(
            previous = chain.blocks.lastOrNull()?.hash,
            height = chain.height,
            difficulty = chain.difficulty
        ).apply {
            val reward = Consensus.reward(chain.height) + txs.fees
            val coinbase = Transaction.coinbase(reward, coldWallet)
            transactions += coinbase
            transactions += txs
        }
        return blk.copy(nonce = mine(blk))
    }

}
