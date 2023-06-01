package generator.model.flight

import generator.model.airline.Airline
import kotlinx.serialization.Serializable

@Serializable
data class FlightIdentification(
    val id: String,
    val callsign: String
)

@Serializable
data class FlightStatus(val live: String)

@Serializable
data class TimeData(
    val departure: Long?,
    val arrival: Long?
)

@Serializable
data class TimeOther(
    val eta: Long,
    val updated: Long
)

@Serializable
data class TimeHistorical(
    // has to be spelled 'flighttime' and not 'flightTime' because of API
    val flighttime: String,
    val delay: String
)

@Serializable
data class FlightTime(
    val scheduled: TimeData,
    val real: TimeData,
    val estimated: TimeData,
    val other: TimeOther,
    val historical: TimeHistorical
)

@Serializable
data class FlightTrail(
    val lat: Float,
    val lng: Float,
    val alt: Int,
    val spd: Int,
    val hd: Int
)

@Serializable
data class Flight(
    val identification: FlightIdentification,
    val status: FlightStatus,
    val owner: Airline? ,
    val airspace: String? = null,
    val time: FlightTime,
    val trail: List<FlightTrail>,
    val firstTimestamp: Long
)
