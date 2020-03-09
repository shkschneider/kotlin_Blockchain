package me.shkschneider.blockchain

import me.shkschneider.crypto.Hmac
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.sign
import me.shkschneider.data.Address
import me.shkschneider.data.Coin
import me.shkschneider.data.Timestamp
import me.shkschneider.data.toHex
import me.shkschneider.data.Hex
import me.shkschneider.data.fromHex
import me.shkschneider.data.timestamp
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class TransactionOutput(
    val to: Address,
    val amount: Coin,
    val time: Timestamp = timestamp
) {

    val lockScript: Hex = Hmac.sign(to.publicKey.encoded, data).toHex()
    var unlockScript: Hex? = null

    val isClaimed: Boolean get() = unlockScript != null

    fun unlock(privateKey: PrivateKey) {
        unlockScript = privateKey.sign(lockScript.fromHex()).toHex()
    }

    val data: ByteArray get() = stringOf(
        time,
        to,
        amount.sat
    ).toByteArray()

    override fun toString(): String = "TransactionOutput {" + stringOf(
        " time=$time",
        " to=$to",
        " amount=[bit=${amount.bit},sat=${amount.sat}]",
        " isClaimed=$isClaimed"
    ) + " }"

    companion object {

        fun coinbase(reward: Coin, coldWallet: ColdWallet): TransactionOutput =
            TransactionOutput(
                to = coldWallet.address(),
                amount = reward
            )

    }
}
