package generator.model

import kotlinx.serialization.Serializable

@Serializable
data class Code(
    val iata: String,
    val icao: String
)
