package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Arrivals(
    val date: String,
    val plan: String,
    val expec: String,
    val dest: String,
    val flightNum: String,
    val flightStatus: String
)
