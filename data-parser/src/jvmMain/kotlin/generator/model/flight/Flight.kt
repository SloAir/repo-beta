package generator.model.flight

import generator.model.airline.Airline
import kotlinx.serialization.Serializable

@Serializable
data class FlightIdentification(
    var id: String,
    var callsign: String
)

@Serializable
data class FlightStatus(var live: String)

@Serializable
data class TimeData(
    var departure: Long?,
    var arrival: Long?
)

@Serializable
data class TimeOther(
    var eta: Long,
    var updated: Long
)

@Serializable
data class TimeHistorical(
    // has to be spelled 'flighttime' and not 'flightTime' because of API
    var flighttime: String,
    var delay: String
)

@Serializable
data class FlightTime(
    var scheduled: TimeData,
    var real: TimeData,
    var estimated: TimeData,
    var other: TimeOther,
    var historical: TimeHistorical
)

@Serializable
data class FlightTrail(
    var lat: Float,
    var lng: Float,
    var alt: Int,
    var spd: Int,
    var ts: Long,
    var hd: Int
)

@Serializable
data class Flight(
    val id: Int,
    var identification: FlightIdentification,
    var status: FlightStatus,
    var owner: Airline,
    var airspace: String? = null,
    var time: FlightTime,
    var trail: List<FlightTrail>,
    var firstTimestamp: Long
)
