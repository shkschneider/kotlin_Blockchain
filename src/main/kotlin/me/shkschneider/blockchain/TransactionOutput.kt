package me.shkschneider.blockchain

import me.shkschneider.data.Coin
import me.shkschneider.data.Address
import me.shkschneider.data.Data
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class TransactionOutput internal constructor(
    val to: Address,
    val amount: Coin
) : Data() {

    override fun data(): ByteArray = stringOf(to, amount).toByteArray()

    override fun toString(): String = "TransactionOutput {" + stringOf(
        " to=$to",
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
