import data.model.Arrivals
import data.model.Departures
import it.skrape.core.*
import it.skrape.fetcher.*

fun main() {
    try {
        val scrapedData = skrape(HttpFetcher) {
            request {
                url = "https://www.lju-airport.si/sl/leti/odhodi-in-prihodi/"
            }
            val departureData = mutableListOf<Departures>()
            val arrivalsData = mutableListOf<Arrivals>()

            val strArray = arrayOf("arrivals","departures")

            extract {
                htmlDocument {
                    strArray.forEach { str ->
                        findFirst("div.js-tab.${str}"){
                            val test = this.children
                            val tabDay = test.find { it.hasClass("js-tab-day") && it.hasClass("selected")}
                            val tabDayChildren = tabDay?.children
                            val tableList = tabDayChildren?.find { it.hasClass("quick-flights-table") }
                            val tableListChildren = tableList?.children
                            val quickFlightsList = tableListChildren?.find { it.hasClass("quick-flights-table-list") }
                            val flights = quickFlightsList?.children
                            val flightsDiv = flights?.filter { it.hasClass("quick-flights-item") && it.hasClass("js-search-row") }
                            if(flightsDiv != null) {
                                flightsDiv.forEach { div ->
                                    val elements = div.allElements
                                    val date = elements.find { it.hasClass("date") && it.hasClass("f-hide-on-mobile") }?.text ?: "empty"
                                    val planned = elements.find { it.hasClass("planned") }?.text?.split(" ")?.get(0) ?: "empty"
                                    val expected = elements.find { it.hasClass("expected") }?.text?.trim()
                                        ?.takeUnless { it.isBlank() } ?: "empty"
                                    var flightNumber = "empty"
                                    val destination = elements.find { it.hasClass("destination") }?.let { destElement ->
                                        flightNumber = destElement.findFirst("span.flight-num").text
                                        destElement.text.replace(flightNumber, "").trim()
                                    } ?: "empty"
                                    val exit = elements.find { it.hasClass("exit") && it.hasClass("f-hide-on-mobile") }?.text ?: "empty"
                                    val check = elements.find { it.hasClass("checkin") && it.hasClass("f-hide-on-mobile") }?.text ?: "empty"
                                    val status = elements.find { it.hasClass("status") }?.let {
                                        val text = it.text.trim()
                                        text.ifBlank { "empty" }
                                    } ?: "empty"
                                    if(str == "departures") {
                                        departureData.add(
                                            Departures(
                                                date = date,
                                                plan = planned,
                                                expec = expected,
                                                dest = destination,
                                                flightNum = flightNumber,
                                                exitTag = exit,
                                                checkIn = check,
                                                flightStatus = status
                                            )
                                        )
                                    } else {
                                        arrivalsData.add(
                                            Arrivals(
                                                date = date,
                                                plan = planned,
                                                expec = expected,
                                                dest = destination,
                                                flightNum = flightNumber,
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
            arrivalsData to departureData
        }
        println("Arrivals")
        scrapedData.first.forEach { flight ->
            println("${flight.date} - ${flight.plan} - ${flight.expec} - ${flight.dest} - ${flight.flightNum} - ${flight.flightStatus}")
        }
        println("Departures")
        scrapedData.second.forEach { flight ->
            println("${flight.date} - ${flight.plan} - ${flight.expec} - ${flight.dest} - ${flight.flightNum} - ${flight.exitTag} - ${flight.checkIn} - ${flight.flightStatus}")
        }
    } catch (ex : Exception) {
        println(ex.message)
    }
}