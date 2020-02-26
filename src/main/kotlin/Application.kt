import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin
import me.shkschneider.participants.ColdWallet
import me.shkschneider.participants.HotWallet
import me.shkschneider.participants.Node
import me.shkschneider.print

object Application {

    private fun supplyLimit(): Coin {
        var height = 0
        var coins = Consensus.reward(height)
        while (true) {
            val reward = Consensus.reward(height++)
            if (reward.sat <= 1) break
            coins += reward
        }
        return coins
    }

    @JvmStatic
    fun main(vararg argv: String) {
        println("Version: " + Consensus.version)
        println("SupplyLimit: " + supplyLimit())

        println()

        val chain = Chain()
        println(chain)
        println()
        val coldWallet = ColdWallet(Consensus.origin.private, Consensus.origin.public)
        println(coldWallet)
        val hotWallet1 = HotWallet.Factory(chain)
        println(hotWallet1)

        chain.add(Transaction(
            inputs = Consensus.genesis.outputs.toMutableList(),
            outputs = mutableListOf(TransactionOutput.coinbase(Consensus.reward(chain.height), hotWallet1))
        ).apply { sign(Consensus.origin.private) })
        val node1 = Node(chain, hotWallet1)
        chain.add(node1.mine().also { it.print() })
        val hotWallet2 = HotWallet.Factory(chain)
        val node2 = Node(chain, hotWallet2)
        hotWallet1.send(to = hotWallet2.address(), amount = Coin(sat = 42), fees = Coin(
            sat = 1
        )
        )
        chain.add(node2.mine().also { it.print() })

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
        hotWallet1.balance() == (chain.blocks[0].outputs.toCoin() + Consensus.reward(1) - 42 - 1) || throw BlockchainException()
        hotWallet2.balance() == (Consensus.reward(2) + 42 + 1) || throw BlockchainException()
    }

}
