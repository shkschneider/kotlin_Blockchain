package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.blk
import me.shkschneider.blockchain.mine
import me.shkschneider.consensus.validate
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.random.Random

private const val times = 10

@RunWith(Parameterized::class)
class MiningTest(private val blk: Block) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = mutableListOf<Block>().apply {
            repeat(times) {
                add(blk(height = Random.nextInt(1, 100)))
            }
        }

    }

    @Test
    fun mining() {
        blk.mine().validate()
    }

}
