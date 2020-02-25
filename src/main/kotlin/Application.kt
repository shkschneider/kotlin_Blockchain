import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.Consensus
import me.shkschneider.crypto.Coin
import me.shkschneider.participants.ColdWallet
import me.shkschneider.participants.HotWallet
import me.shkschneider.participants.Node
import me.shkschneider.print

object Application {

    @JvmStatic
    fun main(vararg argv: String) {
        println("Version: " + Consensus.version)
        println(Consensus.reward)

        println()

        val chain = Chain()
        println(chain)
        println()
        val coldWallet = ColdWallet(Consensus.origin.private, Consensus.origin.public)
        println(coldWallet)
        val hotWallet = HotWallet.Factory(chain, "seed")
        println(hotWallet)

        chain.add(Transaction(
            inputs = Consensus.genesis.outputs.toMutableList(),
            outputs = mutableListOf(TransactionOutput(to = hotWallet.address(), amount = Consensus.reward))
        ).apply { sign(Consensus.origin.private) })
        val node = Node(chain, hotWallet)
        chain.add(node.mine().also { it.print() })
        hotWallet.send(to = coldWallet.address(), amount = Coin(sat = 42))
        chain.add(node.mine().also { it.print() })

        println()
        println(chain)
        println()
        chain.blocks.forEach { it.print() }
        if (chain.mempool.isNotEmpty()) {
            println()
            chain.mempool.forEach { it.print() }
        }
        if (chain.utxos.isNotEmpty()) {
            println()
            chain.utxos.forEach { it.print() }
        }
        println(hotWallet)
    }

}
