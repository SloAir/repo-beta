package generator.model.airport

import generator.model.Code
import kotlinx.serialization.Serializable

@Serializable
data class AirportCountry(
    val id: Int,
    val name: String,
    val code: String
)

@Serializable
data class AirportRegion(val city: String)

@Serializable
data class AirportPostion(
    val latitude: Float,
    val longitude: Float,
    val altitude: Int,
    val country: AirportCountry,
    val region: AirportRegion
)

@Serializable
data class AirportTimezone(
    val name: String,
    val offset: Int,
    val offsetHours: String,
    val abbr: String,
    val abbrName: String,
    val isDst: Boolean
)

@Serializable
data class Airport(
    val name: String,
    val code: Code,
    val position: AirportPostion,
    val isVisible: Boolean,
    val website: String,
)
