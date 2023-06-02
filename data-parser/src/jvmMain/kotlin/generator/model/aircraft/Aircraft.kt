package generator.model.aircraft

import kotlinx.serialization.Serializable

@Serializable
data class AircraftModel(
    var code: String,
    var text: String
)

@Serializable
data class AircraftImage(
    var src: String,
    var link: String,
    var copyright: String,
    var source: String
)

@Serializable
data class FlightHistory(var flightId: String)

@Serializable
data class Aircraft(
    var id: Int,
    var model: AircraftModel,
    var registration: String,
    var images: List<AircraftImage>,
    var flightHistory: List<FlightHistory>
)