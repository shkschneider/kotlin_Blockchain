import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.BlockchainException
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.data.Coin
import me.shkschneider.data.toCoin
import me.shkschneider.participants.ColdWallet
import me.shkschneider.participants.HotWallet
import me.shkschneider.participants.Node
import me.shkschneider.print
import java.util.concurrent.TimeUnit

object Application {

    @JvmStatic
    fun main(vararg argv: String) {
        val chain = Chain()
        println(chain.estimatedSupply())
        println(chain)
        val coldWallet = ColdWallet(Consensus.origin.private, Consensus.origin.public)
        println(coldWallet)
        val hotWallet1 = HotWallet.Factory(chain)
        println(hotWallet1)

        val tx = Transaction(
            inputs = mutableListOf(chain.blocks.flatMap { it.outputs }.first()),
            outputs = mutableListOf(TransactionOutput.coinbase(Consensus.Rules.reward(chain.height), hotWallet1))
        ).apply {
            unlock(Consensus.origin.private)
            sign(Consensus.origin.private)
        }
        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        chain.add(tx)
        chain.add(tx)
        val node1 = Node(chain, hotWallet1)
        chain.add(node1.mine())
        val hotWallet2 = HotWallet.Factory(chain)
        val node2 = Node(chain, hotWallet2)
        hotWallet1.send(to = hotWallet2.address(), amount = Coin(sat = 42), fees = Coin(sat = 1))
        chain.add(node2.mine())

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
        hotWallet1.balance() == (chain.blocks[0].outputs.toCoin() + Consensus.Rules.reward(1) - 42 - 1) || throw BlockchainException.WalletException(
            "balance"
        )
        hotWallet2.balance() == (Consensus.Rules.reward(2) + 42 + 1) || throw BlockchainException.WalletException("balance")
        chain.validate()
    }

}
