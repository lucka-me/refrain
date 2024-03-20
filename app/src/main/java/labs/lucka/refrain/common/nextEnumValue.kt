package labs.lucka.refrain.common

inline fun <reified T: Enum<T>> T.next(): T {
    val values = enumValues<T>()
    return values[(ordinal + 1) % values.size]
}