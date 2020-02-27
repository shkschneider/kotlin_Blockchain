package me.shkschneider.blockchain

import me.shkschneider.data.Coin
import me.shkschneider.crypto.Hmac
import me.shkschneider.crypto.PrivateKey
import me.shkschneider.crypto.sign
import me.shkschneider.crypto.verify
import me.shkschneider.data.Address
import me.shkschneider.data.Data
import me.shkschneider.data.Hex
import me.shkschneider.data.fromHex
import me.shkschneider.data.toBase64
import me.shkschneider.data.toHex
import me.shkschneider.participants.ColdWallet
import me.shkschneider.stringOf

data class TransactionOutput(
    val to: Address,
    val amount: Coin
) : Data() {

    private val lockScript: Hex = Hmac.sign(to.publicKey.encoded, data).toHex()
    var unlockScript: Hex? = null

    val isClaimed: Boolean get() = unlockScript != null && verify()

    fun unlock(privateKey: PrivateKey) {
        unlockScript = privateKey.sign(data + lockScript.fromHex()).toHex()
    }

    fun verify(): Boolean {
        unlockScript?.let {
            if (!to.publicKey.verify(data + lockScript.fromHex(), it.fromHex())) return false
        }
        return true
    }

    override fun data(): ByteArray = stringOf(
        time,
        to.publicKey.encoded.toBase64(),
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
                coldWallet.address(),
                reward
            )

    }
}
