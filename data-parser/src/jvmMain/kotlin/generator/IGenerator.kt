package generator

import kotlinx.serialization.json.JsonArray

interface IGenerator<T> {
    fun generateOne(): T
    fun generate(len: Int): List<T>
    fun serialize(arr: List<T>): String
}
