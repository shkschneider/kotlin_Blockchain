package me.shkschneider.data

abstract class Data(val time: Timestamp = timestamp) : Comparable<Data> {

    val data: ByteArray get() = data()

    protected abstract fun data(): ByteArray

    override fun hashCode(): Int = data.hashCode() // possible duplicates

    override fun equals(other: Any?): Boolean = when (other) {
        is Data -> compareTo(other) == 0
        else -> false
    }

    override fun compareTo(other: Data): Int = if (data.contentEquals(other.data)) 0 else -1

}
