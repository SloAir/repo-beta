package generator.model.aircraft

data class AircraftModel(
    val code: String,
    val text: String
)

data class AircraftImage(
    val src: String,
    val link: String,
    val copyright: String,
    val source: String
)

data class FlightHistory(val flightId: String)

data class Aircraft(
    val model: AircraftModel,
    val registration: String,
    val images: List<AircraftImage>,
    val flightHistory: List<FlightHistory>
)