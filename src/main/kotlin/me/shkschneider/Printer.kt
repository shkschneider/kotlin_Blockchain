package me.shkschneider

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput

internal fun TransactionOutput.print() {
    println(this)
}

internal fun Transaction.print() {
    println(this)
    inputs.forEach { txo ->
        println(" <- $txo")
    }
    outputs.forEach { txo ->
        println(" -> $txo")
    }
}

internal fun Block.print() {
    println(this)
    transactions.forEach { tx ->
        println(" $tx")
        tx.inputs.forEach { txo ->
            println("  <- $txo")
        }
        tx.outputs.forEach { txo ->
            println("  -> $txo")
        }
    }
}

internal fun Chain.print() {
    println(this)
    blocks.forEach { it.print() }
    mempool.forEach { it.print() }
    utxos.forEach { it.print() }
}
