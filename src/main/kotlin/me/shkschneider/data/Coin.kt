package me.shkschneider.data

import me.shkschneider.blockchain.Transaction
import me.shkschneider.blockchain.TransactionOutput
import me.shkschneider.stringOf
import kotlin.math.roundToInt

internal fun List<TransactionOutput>.toCoin(): Coin =
    Coin(sat = sumBy { it.amount.sat })

internal val List<Transaction>.fees: Coin
    get() = Coin(sat = sumBy { it.fees.sat })

/**
 * 1 bit =     100 cent
 *       =   1 000 milli
 *       =  10 000 micro
 *       = 100 000 sat
 * 1 sat = 0.1     micro
 *       = 0.01    milli
 *       = 0.001   cent
 *       = 0.00001 bit
 */
data class Coin(
    val sat: Int
) : Comparable<Coin> {

    constructor(bit: Double) : this((bit * 100_000).roundToInt())

    val bit: Double = sat.div(100_000.toDouble())

    val cent: Double = sat.div(1_000.toDouble())

    val milli: Double = sat.div(100.toDouble())

    val micro: Double = sat.div(10.toDouble())

    override fun compareTo(other: Coin): Int = this.sat.compareTo(other.sat)

    operator fun compareTo(other: Number): Int = this.sat.compareTo(other.toInt())

    operator fun plus(other: Coin): Coin = Coin(this.sat + other.sat)

    operator fun plus(other: Number): Coin = Coin(this.sat + other.toInt())

    operator fun minus(other: Coin): Coin = Coin(this.sat - other.sat)

    operator fun minus(other: Number): Coin = Coin(this.sat - other.toInt())

    operator fun times(other: Coin): Coin = Coin(this.sat * other.sat)

    operator fun times(other: Number): Coin = Coin(this.sat * other.toInt())

    operator fun div(other: Coin): Coin = Coin(this.sat / other.sat)

    operator fun div(other: Number): Coin = Coin(this.sat / other.toInt())

    override fun toString(): String = "Coin {" + stringOf(
        " bit=$bit",
        " cent=$cent",
        " milli=$milli",
        " micro=$micro",
        " sat=$sat"
    ) + " }"

}
