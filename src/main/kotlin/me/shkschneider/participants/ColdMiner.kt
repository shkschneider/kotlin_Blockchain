package me.shkschneider.participants

import me.shkschneider.blockchain.Block
import me.shkschneider.crypto.difficulty
import kotlin.random.Random

open class ColdMiner {

    fun mine(blk: Block): Long {
        while (true) {
            val nonce = Random.nextLong()
            if (blk.copy(nonce = nonce).hash.difficulty >= blk.difficulty)
                return nonce
        }
    }

}
