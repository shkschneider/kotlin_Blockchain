package me.shkschneider.blockchain

import me.shkschneider.consensus.Consensus
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyTest {

    @Test
    fun `genesis difficulty`() {
        with(blk(0)) {
            assertEquals(Consensus.genesis.difficulty, difficulty)
        }
    }

    @Test
    fun `difficulty after 1 having`() {
        with(blk(Consensus.Rules.halving)) {
            assertTrue { difficulty > Consensus.genesis.difficulty }
            assertEquals(Consensus.Rules.difficulty(height), difficulty)
        }
        with(blk(Consensus.Rules.halving + 1)) {
            assertTrue { difficulty > Consensus.genesis.difficulty }
            assertEquals(Consensus.Rules.difficulty(height), difficulty)
        }
    }

    @Test
    fun `difficulty after 10 having`() {
        with(blk(Consensus.Rules.halving * 10)) {
            assertTrue { difficulty > Consensus.genesis.difficulty }
            assertEquals(Consensus.Rules.difficulty(height), difficulty)
        }
    }

}
