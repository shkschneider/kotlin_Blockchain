package me.shkschneider.consensus

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.data.toCoin

fun TransactionOutput.validate() {
    verify() || throw BlockchainException.TransactionOutputException("verify")
    amount.sat > 0 || throw BlockchainException.TransactionOutputException("amount")
}

fun Transaction.validate() {
    if (isCoinbase) {
        inputs.isEmpty() || throw BlockchainException.TransactionException("coinbase inputs")
        outputs.toCoin() > 0 || throw BlockchainException.TransactionException("coinbase outputs")
    } else {
        inputs.toCoin() >= outputs.toCoin() || throw BlockchainException.TransactionException("inputs/outputs")
        verify() == true || throw BlockchainException.TransactionException("verify")
    }
    inputs.forEach { it.validate() }
    inputs.all { it.unlockScript != null } || throw BlockchainException.TransactionException("inputs unlockScript")
    outputs.forEach { it.validate() }
    isSigned || throw BlockchainException.TransactionException("signature")
}

fun Block.validate() {
    if (height == 0) {
        previous == Consensus.genesis.previous || throw BlockchainException.BlockException("previous")
        difficulty == Consensus.genesis.difficulty || throw BlockchainException.BlockException("difficulty")
    }
    height >= 0 || throw BlockchainException.BlockException("height")
    coinbase.isCoinbase || throw BlockchainException.BlockException("coinbase")
    transactions.count { it.isCoinbase } == 1 || throw BlockchainException.BlockException("coinbases")
    size <= Consensus.Rules.blockSize || throw BlockchainException.BlockException("size")
    transactions.forEach { it.validate() }
}

fun Chain.validate() {
    mempool.forEach { tx ->
        tx.inputs.all { it.isClaimed } || throw BlockchainException.ChainException("mempool inputs isClaimed")
        tx.validate()
    }
    blocks.forEach { it.validate() }
    utxos.forEach { it.validate() }
    blocks.flatMap { it.outputs }.filter { it.unlockScript == null }.size == utxos.size || throw BlockchainException.ChainException("blocks unlockScripts!=utxos.size")
    amount.sat > 0 || throw BlockchainException.ChainException("amount")
}
