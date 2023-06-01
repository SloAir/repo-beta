package generator.model.aircraft

import kotlinx.serialization.Serializable

@Serializable
data class AircraftModel(
    val code: String,
    val text: String
)

@Serializable
data class AircraftImage(
    val src: String,
    val link: String,
    val copyright: String,
    val source: String
)

@Serializable
data class FlightHistory(val flightId: String)

@Serializable
data class Aircraft(
    val model: AircraftModel,
    val registration: String,
    val images: List<AircraftImage>,
    val flightHistory: List<FlightHistory>
)