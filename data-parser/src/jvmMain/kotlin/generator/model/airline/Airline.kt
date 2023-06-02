package generator.model.airline

import generator.model.Code
import kotlinx.serialization.Serializable

@Serializable
data class Airline(
    val id: Int,
    val name: String,
    val short: String,
    val code: Code,
    val url: String
)
