package generator

import generator.model.Code
import generator.model.aircraft.*
import generator.model.airline.*
import generator.model.airport.*
import generator.model.flight.*
import generator.model.readFromFile
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlin.random.Random
import kotlin.random.nextInt

object Generator {
    private val currentDirectory = System.getProperty("user.dir")

    object AircraftGenerator: IGenerator<Aircraft> {

        private fun generateAircraftModel(): AircraftModel {
            val aircraftModels = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/aircraft_models.txt")
            val aircraftModelInitials = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/aircraft_model_initials.txt")

            val random = Random.nextInt(0, aircraftModels.size)
            val aircraftModel = aircraftModels[random]
            val aircraftModelInitial = aircraftModelInitials[random]

            return AircraftModel(aircraftModel, aircraftModelInitial)
        }

        private fun generateRandomRegistrationNumber(): String {
            val randomPos = Random.nextInt(0, 1000)

            val letters = listOf('A' .. 'Z')
            var registrationNumber = ""

            when(randomPos % 2) {
                0 -> {
                    registrationNumber += letters.flatten().random()
                    registrationNumber += "-"
                    for(i in 0 until 4) {
                        registrationNumber += letters.flatten().random()
                    }
                }
                1 -> {
                    for(i in 0 until 2) {
                        registrationNumber += letters.flatten().random()
                    }
                    registrationNumber += "-"
                    for(i in 0 until 3) {
                        registrationNumber += letters.flatten().random()
                    }
                }
            }

            return registrationNumber
        }

        private fun generateAircraftImages(count: Int): List<AircraftImage> {
            val aircraftImages: MutableList<AircraftImage> = mutableListOf()

            val imageSrc = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/images/src.txt")
            val imageLink = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/images/link.txt")
            val imageCopyright = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/images/copyright.txt")
            val imageSource = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/aircraft/data/images/source.txt")

            val generatedNumbers: MutableList<Int> = mutableListOf()

            for(i in 0 until count) {
                val random = Random.nextInt(0, imageSrc.size)

                if(generatedNumbers.contains(random)) {
                    continue
                }

                val src = imageSrc[random]
                val link = imageLink[random]
                val copyright = imageCopyright[random]
                val source = imageSource[random]

                generatedNumbers.add(random)

                aircraftImages.add(
                    AircraftImage(
                        src = src,
                        link = link,
                        copyright = copyright,
                        source = source
                    )
                )
            }

            return aircraftImages
        }

        private fun generateFlightHistory(count: Int): List<FlightHistory> {
            val flightHistory: MutableList<FlightHistory> = mutableListOf()

            for(i in 0 until count) {
                var flightId = ""

                val repeat = 8
                for(j in 0 until repeat) {
                    val random = listOf('0' .. '9', 'a' .. 'z').flatten().random()
                    flightId += random

                }
                flightHistory.add(
                    FlightHistory(flightId)
                )
            }

            return flightHistory
        }

        override fun generate(count: Int): List<Aircraft> {
            val aircrafts: MutableList<Aircraft> = mutableListOf()

            for(i in 0 until count) {
                aircrafts.add(
                    Aircraft(
                        model = generateAircraftModel(),
                        registration = generateRandomRegistrationNumber(),
                        images = generateAircraftImages(5),
                        flightHistory = generateFlightHistory(5)
                    )
                )
            }

            return aircrafts
        }

        override fun serialize(arr: List<Aircraft>): String {
            return Json.encodeToString(ListSerializer(Aircraft.serializer()), arr)
        }
    }

    object AirlineGenerator: IGenerator<Airline> {

        private fun generateAirlineNameShort(name: String): String {
            var short = ""

            name.forEach { c ->
                if(c.isUpperCase()) {
                    short += c
                }
            }

            return short
        }

        private fun generateAirlineName(): String {
            var name = ""

            val airlinesList = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/airline/data/airlines.txt")
            val adjectiveList = listOf(
                "Airways",
                "Airlines",
                "Air",
                "Fly"
            )

            val randomAirline = Random.nextInt(0, airlinesList.size)
            val randomAdjective = Random.nextInt(0, adjectiveList.size)

            val airline = airlinesList[randomAirline]

            when(val adjective = adjectiveList[randomAdjective]) {
                "Airways", "Airlines" -> name = "$airline $adjective"
                "Air" -> {
                    val random = Random.nextInt(0, 1000)
                    when(random % 3) {
                        0 -> name = "$adjective $airline"
                        1 -> name = "$adjective$airline"
                        2 -> name = "$airline$adjective"
                    }
                }
                "Fly" -> name = "$adjective$airline"
            }

            return name
        }

        private fun generateAirlineCode(): Code {
            // IATA
            var iata = ""
            for(i in 0 until 2) {
                val random = listOf('A' .. 'Z').flatten().random()
                iata += random
            }

            // ICAO
            var icao = ""
            for(i in 0 until 3) {
                val random = listOf('A' .. 'Z').flatten().random()
                icao += random
            }

            return Code(iata, icao)
        }

        private fun generateUrl(name: String): String {
            var url = ""

            val tmp = name.replace(" ", "")
            val size = tmp.length
            for(i in 1 until size) {
                if(tmp[i].isUpperCase()) {
                    val left = tmp.substring(0, i)
                    val right = tmp.substring(i, size)
                    url = "$left-$right"
                }
            }

            return url.lowercase()
        }

        fun generateOne(): Airline {
            val airlineName = generateAirlineName()

            return Airline(
                name = airlineName,
                short = generateAirlineNameShort(airlineName),
                code = generateAirlineCode(),
                url = generateUrl(airlineName)
            )
        }

        override fun generate(count: Int): List<Airline> {
            val airlines: MutableList<Airline> = mutableListOf()

            for(i in 0 until count) {
                val airlineName = generateAirlineName()
                airlines.add(
                    Airline(
                        name = airlineName,
                        short = generateAirlineNameShort(airlineName),
                        code = generateAirlineCode(),
                        url = generateUrl(airlineName)
                    )
                )
            }

            return airlines
        }

        override fun serialize(arr: List<Airline>): String {
            return Json.encodeToString(ListSerializer(Airline.serializer()), arr)
        }
    }

    object AirportGenerator: IGenerator<Airport> {

        private fun generateAirportName(): String {
            val airportNames = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/airport/data/airports.txt")
            val random = Random.nextInt(0, airportNames.size)

            return airportNames[random]
        }

        private fun generateAirportCode(): Code {
            // IATA
            var iata = ""
            for(i in 0 until 3) {
                val random = listOf('A' .. 'Z').flatten().random()
                iata += random
            }

            var icao = ""
            for(i in 0 until 4) {
                val random = listOf('A' .. 'Z').flatten().random()
                icao += random
            }

            return Code(iata, icao)
        }

        private fun generateRandomCountry(): AirportCountry {
            val countries = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/airport/data/countries.txt")
            val countryId = Random.nextInt(0, countries.size)
            val country = countries[countryId]
            val countryCode = country.substring(0, 3).uppercase()

            return AirportCountry(
                countryId,
                country,
                countryCode
            )
        }

        private fun generateRandomRegion(): AirportRegion {
            val cities = readFromFile("$currentDirectory/src/jvmMain/kotlin/generator/model/airport/data/cities.txt")
            val random = Random.nextInt(0, cities.size)
            val city = cities[random]

            return AirportRegion(city)
        }

        private fun generateAirportPosition(): AirportPostion {
            val latitude = (Random.nextInt(-9000000 .. 9000000).toFloat() / 100000)
            val longitude = (Random.nextInt(-18000000 .. 18000000).toFloat() / 100000)
            val altitude = Random.nextInt(0, 5000)


            return AirportPostion(
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                country = generateRandomCountry(),
                region = generateRandomRegion()
            )
        }

        private fun generateRandomWebsite(airportName: String): String {
            val uri = airportName.lowercase().replace(" ", "")
            val domains = listOf(
                ".com",
                ".net",
                ".org",
                ".io",
                ".co",
                ".blog",
                ".travel",
                ".cafe",
                ".club",
                ".xyz",
                ".pro",
                ".money",
                ".pet",
                ".help",
                ".gallery",
                ".design",
                ".read",
                ".social",
                ".edu",
                ".gov"
            )

            val random = Random.nextInt(0, domains.size)
            val domain = domains[random]

            return "https://www.$uri$domain"
        }

        override fun generate(count: Int): List<Airport> {
            val airports: MutableList<Airport> = mutableListOf()

            for(i in 0 until count) {
                val airportName = generateAirportName()
                airports.add(
                    Airport(
                        name = airportName,
                        code = generateAirportCode(),
                        position = generateAirportPosition(),
                        isVisible = true,
                        website = generateRandomWebsite(airportName),
                    )
                )
            }

            return airports
        }


        override fun serialize(arr: List<Airport>): String {
            return Json.encodeToString(ListSerializer(Airport.serializer()), arr)
        }
    }

    object FlightGenerator: IGenerator<Flight> {

        private fun generateFlightIdentification(): FlightIdentification {
            var id = ""
            val idLen = 8
            val idCharacters = listOf('0' .. '9', 'a' .. 'z')

            for(i in 0 until idLen) {
                id += idCharacters.flatten().random()
            }

            var callsign = ""
            val callsignLen = 6
            val callsignCharacters = listOf('0' .. '9', 'A' .. 'Z')

            for(i in 0 until callsignLen) {
                callsign += callsignCharacters.flatten().random()
            }

            return FlightIdentification(
                id = id,
                callsign = callsign
            )
        }

        private fun generateStatus(): FlightStatus {
            var live = false
            val random = Random.nextInt(0, 1000)

            when(random % 3) {
                0, 1 -> live = true
                2 -> live = false
            }

            return FlightStatus(live.toString())
        }

        // generates a random airline for the owner field
        private fun generateAirline(): Airline? {
            val random = Random.nextInt(0, 1000)

            return when(random % 5) {
                0, 1, 2 -> AirlineGenerator.generateOne()
                else -> null
            }
        }

        private fun generateTimeData(str: String): TimeData {
            val field = str.lowercase()

            // time in epoch seconds
            val currentTime = Instant.now().epochSecond
            // 5 seconds
            val minOffset = 60 * 5
            // 3 hours
            val maxOffset = 60 * 60 * 3

            val randomDeparture: Long
            val randomArrival: Long

            return when(field) {
                "scheduled" -> {
                    randomDeparture = currentTime - Random.nextInt(minOffset, maxOffset).toLong()
                    randomArrival = currentTime + Random.nextInt(minOffset, maxOffset).toLong()
                    TimeData(
                        departure = randomDeparture,
                        arrival = randomArrival,
                    )
                }
                "real" -> {
                    randomDeparture = currentTime - Random.nextInt(minOffset, maxOffset).toLong()
                    TimeData(
                        departure = randomDeparture,
                        arrival = null
                    )
                }
                "estimated" -> {
                    randomArrival = currentTime + Random.nextInt(minOffset, maxOffset).toLong()
                    TimeData(
                        departure = null,
                        arrival = randomArrival
                    )
                }
                else -> TimeData(null, null)
            }
        }

        private fun generateOtherTimeField(): TimeOther {
            // time in epoch seconds
            val currentTime = Instant.now().epochSecond
            // 5 seconds
            val minOffset = 60 * 5
            // 3 hours
            val maxOffset = 60 * 60 * 3

            val randomEta = currentTime + Random.nextInt(minOffset, maxOffset).toLong()
            val updated = Instant.now().epochSecond

            return TimeOther(
                eta = randomEta,
                updated = updated
            )
        }

        private fun generateHistoricalTimeField(): TimeHistorical {
            // 30 minutes minimum flying time
            val minFlightTime = 60 * 30
            // 5 hours max flying time
            val maxFlightTime = 60 * 60 * 5

            val minDelay = 0
            // 4 hours
            val maxDelay = 60 * 60 * 4

            val randomFlightTime = Random.nextInt(minFlightTime, maxFlightTime).toString()
            val randomDelay = Random.nextInt(minDelay, maxDelay).toString()

            return TimeHistorical(
                flighttime = randomFlightTime,
                delay = randomDelay
            )
        }

        private fun generateTime(): FlightTime {
            return FlightTime(
                scheduled = generateTimeData("scheduled"),
                real = generateTimeData("real"),
                estimated = generateTimeData("estimated"),
                other = generateOtherTimeField(),
                historical = generateHistoricalTimeField()
            )
        }

        private fun generateFlightTrail(): FlightTrail {
            TODO()
        }

        override fun generate(count: Int): List<Flight> {
            TODO("Not yet implemented")
        }

        override fun serialize(arr: List<Flight>): String {
            TODO()
        }
    }
}

fun main() {
    // Generator.Airport.generate()
    // val aircrafts = Generator.AircraftGenerator.generate(10)
    val airlines = Generator.AirlineGenerator.generate(10)
    // val airports = Generator.AirportGenerator.generate(10)

    // aircrafts.forEach { aircraft ->
    //     println(aircraft)
    // }

    Generator.AirlineGenerator.serialize(airlines)

    // airports.forEach { airport ->
    //     println(airport)
    // }
}