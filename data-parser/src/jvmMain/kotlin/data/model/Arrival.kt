package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Arrival(
    var date: String,
    var planned: String,
    var expected: String,
    var destination: String,
    var flightNumber: String,
    var flightStatus: String
)