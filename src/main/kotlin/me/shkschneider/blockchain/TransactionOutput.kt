package me.shkschneider.blockchain

import me.shkschneider.crypto.Coin
import me.shkschneider.crypto.Hashable
import me.shkschneider.crypto.PublicKey
import me.shkschneider.crypto.toHash
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class TransactionOutput internal constructor(
    val to: PublicKey,
    val amount: Coin
) : Hashable() {

    override fun data(): String = stringOf(to, amount)

    override fun toString(): String = "TransactionOutput {" + stringOf(
        " to=${to.toHash()}",
        " amount=[bit=${amount.bit},sat=${amount.sat}]"
    ) + " }"

    companion object {

        fun coinbase(reward: Coin, coldWallet: ColdWallet): TransactionOutput =
            TransactionOutput(
                coldWallet.address(),
                reward
            )

    }

}
