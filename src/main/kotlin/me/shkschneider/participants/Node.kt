package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.crypto.fees

class Node(
    private val chain: Chain,
    private val hotWallet: HotWallet
) : Miner() {

    fun relay(tx: Transaction) {
        tx.validate()
        if (!chain.mempool.contains(tx)) {
            chain.add(tx)
        }
    }

    fun relay(blk: Block) {
        blk.validate()
        if (!chain.blocks.contains(blk)) {
            chain.add(blk)
        }
    }

    fun mine(): Block {
        val txs = unstack(chain.mempool)
        val blk = Block(
            previous = chain.blocks.lastOrNull()?.hash,
            height = chain.height,
            difficulty = chain.difficulty
        ).apply {
            val reward = Consensus.reward(chain.height) + txs.fees
            val coinbase = Transaction.coinbase(reward, hotWallet)
            transactions += coinbase
            transactions += txs
        }
        return blk.copy(nonce = mine(blk))
    }

}
