import me.shkschneider.blockchain.Chain
import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.consensus.Consensus
import me.shkschneider.consensus.validate
import me.shkschneider.crypto.KeyPair
import me.shkschneider.data.Coin
import me.shkschneider.participants.ColdWallet
import me.shkschneider.participants.HotWallet
import me.shkschneider.participants.Node
import me.shkschneider.stringOf
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object Application {

    @JvmStatic
    fun main(vararg argv: String) {
        val chain = Chain()

        assert(chain.height == 1)
        chain.validate()

        val coldWallet = ColdWallet(Consensus.origin)
        println("coldWallet: $coldWallet")
        val hotWallet1 = HotWallet(chain, KeyPair.Factory())
        println("hotWallet1: $hotWallet1")
        val hotWallet2 = HotWallet(chain, KeyPair.Factory())
        println("hotWallet2: $hotWallet2")
        val node1 = Node(chain, hotWallet1)
        val node2 = Node(chain, hotWallet2)
        println(chain.estimatedSupply())
        println(chain)

        val amount = Consensus.Rules.reward(1) / 2
        chain.add(Transaction(
            inputs = mutableListOf(chain.blocks.flatMap { it.outputs }.first()),
            outputs = mutableListOf(
                TransactionOutput.coinbase(amount, hotWallet1),
                TransactionOutput.coinbase(amount, coldWallet) // change
            )
        ).apply {
            coldWallet.unlock(this)
            coldWallet.sign(this)
        })
        sleep(); chain.add(node1.mine())
        hotWallet1.send(to = hotWallet2.address(), amount = amount / 2, fees = Coin(0))
        sleep(); chain.add(node1.mine())

        assert(chain.height == 2)
        chain.validate()

        hotWallet2.send(to = hotWallet1.address(), amount = Coin(sat = 42), fees = Coin(sat = 1))
        sleep(); chain.add(node2.mine())

        assert(chain.height == 3)
        chain.validate()

        chain.blocks.forEach { it.print() }
        if (chain.mempool.isNotEmpty()) {
            println()
            chain.mempool.forEach { it.print() }
        }
        if (chain.utxos.isNotEmpty()) {
            println()
            chain.utxos.forEach { it.print() }
        }
        chain.validate()
    }

    private fun sleep() = Thread.sleep(TimeUnit.SECONDS.toMillis(1))

}
