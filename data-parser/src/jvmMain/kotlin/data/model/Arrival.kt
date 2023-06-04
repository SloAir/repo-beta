package data.model

import kotlinx.serialization.Serializable

@Serializable
class Arrival(
    override var id: Int,
    override var date: String,
    override var planned: String,
    override var expected: String,
    override var destination: String,
    override var flightNumber: String,
    override var flightStatus: String
): Flight()