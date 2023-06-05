package data.model

data class Departures(
    val date: String,
    val plan: String,
    val expec: String,
    val dest: String,
    val flightNum: String,
    val exitTag: String,
    val checkIn: String,
    val flightStatus: String
)