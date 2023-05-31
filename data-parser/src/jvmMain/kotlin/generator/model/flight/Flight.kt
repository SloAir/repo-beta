package generator.model.flight

import generator.model.airline.Airline

data class FlightIdentification(
    val id: String,
    val callsign: String
)

data class Status(val live: String)

data class Scheduled(
    val departure: Long,
    val arrival: Long
)

data class Real(
    val departure: Long,
    val arrival: Long
)

data class Estimated(
    val departure: Long,
    val arrival: Long
)

data class Other(
    val eta: Long,
    val updated: Long
)

data class Historical(
    // has to be spelled 'flighttime' and not 'flightTime' because of API
    val flighttime: String,
    val delay: String
)

data class Time(
    val scheduled: Scheduled,
    val real: Real,
    val estimated: Estimated,
    val other: Other,
    val historical: Historical
)

data class Trail(
    val lat: Float,
    val lng: Float,
    val alt: Int,
    val spd: Int,
    val hd: Int
)

data class Flight(
    val identification: FlightIdentification,
    val status: Status,
    val owner: Airline? = null,
    val airspace: String? = null,
    val time: Time,
    val trail: List<Trail>,
    val firstTimestamp: Long
)
