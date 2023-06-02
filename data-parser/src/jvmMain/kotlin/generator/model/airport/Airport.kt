package generator.model.airport

import generator.model.Code
import kotlinx.serialization.Serializable

@Serializable
data class AirportCountry(
    val id: Int,
    var name: String,
    var code: String
)

@Serializable
data class AirportRegion(var city: String)

@Serializable
data class AirportPostion(
    var latitude: Float,
    var longitude: Float,
    var altitude: Int,
    var country: AirportCountry,
    var region: AirportRegion
)

@Serializable
data class AirportTimezone(
    var name: String,
    var offset: Int,
    var offsetHours: String,
    var abbr: String,
    var abbrName: String,
    var isDst: Boolean
)

@Serializable
data class Airport(
    val id: Int,
    var name: String,
    var code: Code,
    var position: AirportPostion,
    var isVisible: Boolean,
    var website: String,
)
