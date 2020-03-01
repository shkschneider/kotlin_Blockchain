package me.shkschneider.consensus

sealed class BlockchainException(reason: String? = null) : Throwable(reason) {

    class TransactionOutputException(reason: String?) : BlockchainException(reason)
    class TransactionException(reason: String?) : BlockchainException(reason)
    class BlockException(reason: String?) : BlockchainException(reason)
    class ChainException(reason: String?) : BlockchainException(reason)

    class WalletException(reason: String?) : BlockchainException(reason)

}
