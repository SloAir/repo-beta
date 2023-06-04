package data.model

import kotlinx.serialization.Serializable

// superclass needs to be sealed and has to have variables defined as abstract
// in order to serialize the subclasses
// source: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#sealed-classes
@Serializable
sealed class Flight {
    abstract var id: Int
    abstract var date: String
    abstract var planned: String
    abstract var expected: String
    abstract var destination: String
    abstract var flightNumber: String
    abstract var flightStatus: String
}