package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.consensus.validate
import me.shkschneider.data.Address
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin

class Node(
    private val chain: Chain,
    hotWallet: HotWallet
) : HotMiner(chain, hotWallet) {

    fun balance(address: Address): Coin =
        chain.utxos.filter { it.to == address }.toCoin()

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

    val wallet: ColdWallet = hotWallet

}
