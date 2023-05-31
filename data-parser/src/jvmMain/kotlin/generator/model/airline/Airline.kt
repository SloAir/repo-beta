package generator.model.airline

import generator.model.Code

data class Airline(
    val name: String,
    val short: String,
    val code: Code,
    val url: String
)
