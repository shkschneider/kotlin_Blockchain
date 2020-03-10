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
import java.util.concurrent.TimeUnit

object Application {

    @JvmStatic
    fun main(vararg argv: String) {
        val chain = Chain()
        println(chain.estimatedSupply())
        println(chain)

        assert(chain.height == 1)
        chain.validate()

        val coldWallet = ColdWallet(Consensus.origin)
        println(coldWallet)
        val hotWallet1 = HotWallet(chain, KeyPair.Factory())
        println(hotWallet1)
        val hotWallet2 = HotWallet(chain, KeyPair.Factory())
        println(hotWallet1)
        val node1 = Node(chain, hotWallet1)
        val node2 = Node(chain, hotWallet2)

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

        hotWallet2.flush(hotWallet1.address(), Coin(0))
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
        println(hotWallet1)
        assert(node1.balance(hotWallet1.address()) == node2.balance(hotWallet1.address()))
        assert(node1.balance(hotWallet1.address()).sat == 250_000)
        println(hotWallet2)
        assert(node1.balance(hotWallet2.address()) == node2.balance(hotWallet2.address()))
        assert(node2.balance(hotWallet2.address()).sat == 100_000)
    }

    private fun sleep() = Thread.sleep(TimeUnit.SECONDS.toMillis(1))

}
