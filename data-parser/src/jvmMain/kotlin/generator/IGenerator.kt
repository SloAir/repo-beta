package generator

import kotlinx.serialization.json.JsonArray

interface IGenerator<T> {
    fun generate(count: Int): List<T>
}
