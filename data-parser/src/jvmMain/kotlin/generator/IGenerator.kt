package generator

import kotlinx.serialization.json.JsonArray

interface IGenerator<T> {
    fun generateOne(): T
    fun generate(count: Int): List<T>
    fun serialize(arr: List<T>): String
}
