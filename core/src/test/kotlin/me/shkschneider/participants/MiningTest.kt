package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.blockchain.blk
import me.shkschneider.blockchain.mine
import me.shkschneider.consensus.validate
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

private const val times = 10

@RunWith(Parameterized::class)
class MiningTest(private val blk: Block) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = mutableListOf<Block>().apply {
            repeat(times) {
                add(blk(height = 1))
                add(blk(height = 2))
                add(blk(height = 3))
            }
        }

    }

    @Test
    fun mining() {
        blk.mine().validate()
    }

}
