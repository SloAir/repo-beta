package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Departure(
    var date: String,
    var planned: String,
    var expected: String,
    var destination: String,
    var flightNumber: String,
    var exitTag: String,
    var checkIn: String,
    var flightStatus: String
)