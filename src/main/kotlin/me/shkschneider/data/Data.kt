package me.shkschneider.data

abstract class Data {

    val hash: Hash get() = data.toHash()

    val data: ByteArray get() = data()

    protected abstract fun data(): ByteArray

    override fun hashCode(): Int = data.hashCode()

    override fun equals(other: Any?): Boolean = this.hashCode() == other.hashCode()

}
