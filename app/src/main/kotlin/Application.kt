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

object Application {

    private val chain = Chain()
    private val coldWallet = ColdWallet(Consensus.origin)
    private val hotWallet1 = HotWallet(chain, KeyPair.Factory())
    private val hotWallet2 = HotWallet(chain, KeyPair.Factory())
    private val node1 = Node(chain, hotWallet1)
    private val node2 = Node(chain, hotWallet2)

    @JvmStatic
    fun main(vararg argv: String) {
        println(chain.estimatedSupply())
        println(chain)
        chain.validate()

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
        chain.add(node1.mine())
        hotWallet1.send(to = hotWallet2.address(), amount = amount / 2, fees = Coin(0))
        chain.add(node1.mine())
        chain.validate()

        hotWallet2.flush(hotWallet1.address(), Coin(0))
        chain.add(node2.mine())
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
        assert(node1.balance(hotWallet1.address()).sat == 250_000)
        assert(node2.balance(hotWallet2.address()).sat == 100_000)

        hotWallet2.flush(hotWallet1.address(), fees = Coin(sat = 0))
        repeat(10) {
            chain.add(node2.mine())
        }
        chain.validate()
        chain.print()
        println(hotWallet1)
        println(hotWallet2)
    }

}
