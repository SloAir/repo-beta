package generator.model

import kotlinx.serialization.Serializable

@Serializable
data class Code(
    var iata: String,
    var icao: String
)
