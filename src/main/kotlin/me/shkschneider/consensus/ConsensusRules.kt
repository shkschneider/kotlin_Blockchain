package me.shkschneider.consensus

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.data.toCoin

fun TransactionOutput.validate() {
    amount.sat > 0 || throw BlockchainException()
}

fun Transaction.validate() {
    !isCoinbase || inputs.isEmpty() || throw BlockchainException()
    inputs.forEach { it.validate() }
    outputs.forEach { it.validate() }
    !isCoinbase || outputs.toCoin() > 0 || throw BlockchainException()
    isCoinbase || inputs.toCoin() >= outputs.toCoin() || throw BlockchainException()
    isSigned || throw BlockchainException()
}

fun Block.validate() {
    if (height == 0) {
        previous == Consensus.genesis.previous || throw BlockchainException()
        difficulty == Consensus.genesis.difficulty || throw BlockchainException()
    }
    height >= 0 || throw BlockchainException()
    coinbase.isCoinbase || throw BlockchainException()
    transactions.count { it.isCoinbase } == 1 || throw BlockchainException()
    size <= Consensus.Rules.blockSize || throw BlockchainException()
    transactions.forEach { it.validate() }
}

fun Chain.validate() {
    mempool.forEach { it.validate() }
    blocks.forEach { it.validate() }
    utxos.forEach { it.validate() }
    amount.sat > 0 || throw BlockchainException()
}
