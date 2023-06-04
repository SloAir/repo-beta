package generator.model.airline

import generator.model.Code
import kotlinx.serialization.Serializable

@Serializable
data class Airline(
    val id: Int,
    var name: String,
    var short: String,
    var code: Code,
    var url: String
)
