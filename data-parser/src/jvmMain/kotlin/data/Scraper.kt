package data

import androidx.compose.runtime.snapshots.SnapshotStateList
import data.model.Arrival
import data.model.Departure
import data.model.Flight
import data.model.IdGenerator
import it.skrape.core.*
import it.skrape.fetcher.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun scrapeData(): Pair<SnapshotStateList<Flight>, SnapshotStateList<Flight>>
{
    val departureData = SnapshotStateList<Flight>()
    val arrivalsData = SnapshotStateList<Flight>()
    try
    {
        val scrapedData = skrape(HttpFetcher) {
            request {
                url = "https://www.lju-airport.si/sl/leti/odhodi-in-prihodi/"
            }

            val strArray = arrayOf("arrivals", "departures")

            response {
                htmlDocument {
                    strArray.forEach { str ->
                        findFirst("div.js-tab.${str}") {
                            val test = this.children
                            val tabDay = test.find { it.hasClass("js-tab-day") && it.hasClass("selected") }
                            val tabDayChildren = tabDay?.children
                            val tableList = tabDayChildren?.find { it.hasClass("quick-flights-table") }
                            val tableListChildren = tableList?.children
                            val quickFlightsList = tableListChildren?.find { it.hasClass("quick-flights-table-list") }
                            val flights = quickFlightsList?.children
                            val flightsDiv =
                                flights?.filter { it.hasClass("quick-flights-item") && it.hasClass("js-search-row") }

                            flightsDiv ?: return@findFirst

                            flightsDiv.forEach { div ->
                                val elements = div.allElements
                                val date =
                                    elements.find { it.hasClass("date") && it.hasClass("f-hide-on-mobile") }?.text
                                        ?: "empty"
                                val planned =
                                    elements.find { it.hasClass("planned") }?.text?.split(" ")?.get(0) ?: "empty"
                                val expected = elements.find { it.hasClass("expected") }?.text?.trim()
                                    ?.takeUnless { it.isBlank() } ?: "empty"
                                var flightNumber = "empty"
                                val destination = elements.find { it.hasClass("destination") }?.let { destElement ->
                                    flightNumber = destElement.findFirst("span.flight-num").text
                                    destElement.text.replace(flightNumber, "").trim()
                                } ?: "empty"
                                val exit =
                                    elements.find { it.hasClass("exit") && it.hasClass("f-hide-on-mobile") }?.text
                                        ?: "empty"
                                val check =
                                    elements.find { it.hasClass("checkin") && it.hasClass("f-hide-on-mobile") }?.text
                                        ?: "empty"
                                val status = elements.find { it.hasClass("status") }?.let {
                                    val text = it.text.trim()
                                    text.ifBlank { "empty" }
                                } ?: "empty"
                                if(str == "departures") {
                                    departureData.add(
                                        Departure(
                                            id = IdGenerator.setId(),
                                            date = date,
                                            planned = planned,
                                            expected = expected,
                                            destination = destination,
                                            flightNumber = flightNumber,
                                            exitTag = exit,
                                            checkIn = check,
                                            flightStatus = status
                                        )
                                    )
                                }
                                else {
                                    arrivalsData.add(
                                        Arrival(
                                            id = IdGenerator.setId(),
                                            date = date,
                                            planned = planned,
                                            expected = expected,
                                            destination = destination,
                                            flightNumber = flightNumber,
                                            flightStatus = status
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    catch(ex: Exception) {
        println(ex.message)
    }

    return Pair(arrivalsData, departureData)
}

// function serializes each flight object into a JSON string
// returns a list of all of the serialized objects
fun serialize(flights: SnapshotStateList<Flight>): List<String> {
    val list: MutableList<String> = mutableListOf()

    flights.forEach { flight ->
        list.add(Json.encodeToString(flight))
        println(Json.encodeToString(flight))
    }

    return list
}
