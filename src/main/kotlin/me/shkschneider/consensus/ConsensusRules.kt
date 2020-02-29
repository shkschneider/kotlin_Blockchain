package me.shkschneider.consensus

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.crypto.Hmac
import me.shkschneider.crypto.verify
import me.shkschneider.data.difficulty
import me.shkschneider.data.fromBase64
import me.shkschneider.data.fromHex
import me.shkschneider.data.toCoin

fun TransactionOutput.validate() {
    // ALL txo should have a valid lockScript
    Hmac.verify(to.publicKey.encoded, data, lockScript.fromHex()) ||
        throw BlockchainException.TransactionOutputException("lockScript")
    if (isClaimed) {
        // ALL claimed txo should have unlockScript matching lockScript
        to.publicKey.verify(lockScript.fromHex(), requireNotNull(unlockScript).fromHex()) == true ||
            throw BlockchainException.TransactionOutputException("unlockScript")
    }
    // ALL txo should have positive amount
    amount > 0 || throw BlockchainException.TransactionOutputException("amount")
}

fun Transaction.validate() {
    // ALL tx should be signed
    isSigned || throw BlockchainException.TransactionException("isSigned")
    if (!isCoinbase) {
        // ALL tx with fees should have more inputs than outputs
        inputs.toCoin() == outputs.toCoin() + fees ||
            throw BlockchainException.TransactionException("inputs/outputs/fees")
        // ALL tx should be signed
        inputs.first().to.publicKey.verify(data, requireNotNull(signature).fromBase64()) == true ||
            throw BlockchainException.TransactionException("signature")
    }
    // ALL tx inputs should be claimed
    inputs.all { it.isClaimed } || throw BlockchainException.TransactionException("inputs isClaimed")
    // ALL input should be valid
    inputs.forEach { it.validate() }
    // ALL output should be valid
    outputs.forEach { it.validate() }
}

fun Block.validate() {
    // ALL block should have been mined
    isMined || throw BlockchainException.BlockException("isMined")
    hash.difficulty >= difficulty || throw BlockchainException.BlockException("difficulty")
    if (isGenesis) {
        this == Consensus.genesis.copy(nonce = nonce) || throw BlockchainException.ChainException("genesis")
    } else {
        // ALL block should follow Genesis
        height > Consensus.genesis.height || throw BlockchainException.BlockException("height")
        previous != null || throw BlockchainException.BlockException("previous")
    }
    // ALL block should have a single coinbase tx
    transactions.count { it.isCoinbase } == 1 || throw BlockchainException.BlockException("coinbases")
    // ALL block should respect blockSize
    size <= Consensus.Rules.blockSize || throw BlockchainException.BlockException("size")
    // ALL block should have a valid coinbase tx
    coinbase.isCoinbase || throw BlockchainException.BlockException("coinbase")
    coinbase.amount == Consensus.Rules.reward(height) + fees || throw BlockchainException.BlockException("reward")
    // ALL tx should be valid
    transactions.forEach { it.validate() }
}

fun Chain.validate() {
    mempool.forEach { tx ->
        // NONE waiting tx should have be claimed
        tx.inputs.all { it.isClaimed } || throw BlockchainException.ChainException("mempool inputs isClaimed")
        // ALL tx should be valid
        tx.validate()
    }
    blocks.getOrNull(0)?.height == 0 || throw BlockchainException.ChainException("genesis height")
    blocks.forEachIndexed { i, blk ->
        // ALL blocks should be in ascending height (by 1)
        blk.height == blocks.getOrNull(i - 1)?.height?.plus(1) ?: 0 ||
            throw BlockchainException.ChainException("blocks height")
        // ALL blocks should reference the previous' hash
        blk.previous == blocks.getOrNull(i - 1)?.hash ||
            throw BlockchainException.ChainException("blocks previous")
        // ALL blocks should be valid
        blk.validate()
    }
    // ALL unspent txo from blocks should be in utxos
    blocks.flatMap { it.outputs }.filter { it.unlockScript == null }.size == utxos.size ||
        throw BlockchainException.ChainException("blocks unlockScripts!=utxos.size")
    utxos.forEach { txo ->
        // NONE txo should have been spent as input in a block
        blocks.flatMap { it.inputs }.contains(txo) && throw BlockchainException.ChainException("utxos blocks")
        // ALL txo should be valid
        txo.validate()
    }
    // ALL coins of the chain should be positive
    amount > 0 || throw BlockchainException.ChainException("amount")
}
