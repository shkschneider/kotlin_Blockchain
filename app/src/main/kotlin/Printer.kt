import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput

internal fun TransactionOutput.print() {
    println(toString())
}

internal fun Transaction.print() {
    println(toString() + " #" + hashCode())
    inputs.forEach { txo ->
        println(" <- $txo #${txo.hashCode()}")
    }
    outputs.forEach { txo ->
        println(" -> $txo #${txo.hashCode()}")
    }
}

internal fun Block.print() {
    println(toString() + " #" + hashCode())
    transactions.forEach { tx ->
        println(" $tx #${tx.hashCode()}")
        tx.inputs.forEach { txo ->
            println("  <- $txo #${txo.hashCode()}")
        }
        tx.outputs.forEach { txo ->
            println("  -> $txo #${txo.hashCode()}")
        }
    }
}

internal fun Chain.print() {
    println(toString() + " #" + hashCode())
    blocks.forEach { it.print() }
    mempool.forEach { it.print() }
    utxos.forEach { it.print() }
}
