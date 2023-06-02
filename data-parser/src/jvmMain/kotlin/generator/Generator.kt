package generator

import androidx.compose.runtime.snapshots.SnapshotStateList
import data.model.IdGenerator
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
    private val currentDirectory = System.getProperty("user.dir") + "/src/jvmMain/kotlin/generator"

    object AircraftGenerator: IGenerator<Aircraft> {

        private fun generateAircraftModel(): AircraftModel {
            val aircraftModels = readFromFile("$currentDirectory/model/aircraft/data/aircraft_models.txt")
            val aircraftModelInitials = readFromFile("$currentDirectory/model/aircraft/data/aircraft_model_initials.txt")

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

        private fun generateAircraftImages(len: Int): List<AircraftImage> {
            val aircraftImages: MutableList<AircraftImage> = mutableListOf()

            val imageSrc = readFromFile("$currentDirectory/model/aircraft/data/images/src.txt")
            val imageLink = readFromFile("$currentDirectory/model/aircraft/data/images/link.txt")
            val imageCopyright = readFromFile("$currentDirectory/model/aircraft/data/images/copyright.txt")
            val imageSource = readFromFile("$currentDirectory/model/aircraft/data/images/source.txt")

            val generatedNumbers: MutableList<Int> = mutableListOf()

            for(i in 0 until len) {
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

        private fun generateFlightHistory(len: Int): List<FlightHistory> {
            val flightHistory: MutableList<FlightHistory> = mutableListOf()

            for(i in 0 until len) {
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

        private fun generateFlightHistory(
            min: Int,
            max: Int
        ): List<FlightHistory> {
            val flightHistory: MutableList<FlightHistory> = mutableListOf()

            val len = Random.nextInt(min, max)

            for(i in 0 until len) {
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

        override fun generateOne(): Aircraft {
            return Aircraft(
                id = IdGenerator.setId(),
                model = generateAircraftModel(),
                registration = generateRandomRegistrationNumber(),
                images = generateAircraftImages(5),
                flightHistory = generateFlightHistory(5)
            )
        }

        // Function does nothing
        override fun generate(len: Int): SnapshotStateList<Aircraft> = SnapshotStateList()

        fun generate(
            len: Int,
            images: Int,
            flightHistoryMin: Int,
            flightHistoryMax: Int
        ): SnapshotStateList<Aircraft> {
            val aircrafts = SnapshotStateList<Aircraft>()

            for(i in 0 until len) {
                aircrafts.add(
                    Aircraft(
                        id = IdGenerator.setId(),
                        model = generateAircraftModel(),
                        registration = generateRandomRegistrationNumber(),
                        images = generateAircraftImages(images),
                        flightHistory = generateFlightHistory(flightHistoryMin, flightHistoryMax)
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

            val airlinesList = readFromFile("$currentDirectory/model/airline/data/airlines.txt")
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

        override fun generateOne(): Airline {
            val airlineName = generateAirlineName()

            return Airline(
                id = IdGenerator.id,
                name = airlineName,
                short = generateAirlineNameShort(airlineName),
                code = generateAirlineCode(),
                url = generateUrl(airlineName)
            )
        }

        override fun generate(len: Int): SnapshotStateList<Airline> {
            val airlines = SnapshotStateList<Airline>()

            for(i in 0 until len) {
                val airlineName = generateAirlineName()
                airlines.add(
                    Airline(
                        id = IdGenerator.setId(),
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
            val airportNames = readFromFile("$currentDirectory/model/airport/data/airports.txt")
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
            val countries = readFromFile("$currentDirectory/model/airport/data/countries.txt")
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
            val cities = readFromFile("$currentDirectory/model/airport/data/cities.txt")
            val random = Random.nextInt(0, cities.size)
            val city = cities[random]

            return AirportRegion(city)
        }

        private fun generateAirportPosition(): AirportPostion {
            val divisor = 1_000_000

            // Earth's latitude bounds
            // -90.000000
            val minLat = -90_000_000
            // 90.000000
            val maxLat = 90_000_000

            // Earth's longitude bounds
            // -180.000000
            val minLng = -180_000_000
            // 180.000000
            val maxLng = 180_000_000

            // min and max  altitude of the generated Airport
            val minAlt = 0
            val maxAlt = 5500

            val latitude = (Random.nextInt(minLat .. maxLat).toFloat() / divisor)
            val longitude = (Random.nextInt(minLng .. maxLng).toFloat() / divisor)
            val altitude = Random.nextInt(minAlt, maxAlt)


            return AirportPostion(
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                country = generateRandomCountry(),
                region = generateRandomRegion()
            )
        }

        private fun generateAirportPosition(
            minLat: Int,
            maxLat: Int,
            minLng: Int,
            maxLng: Int,
            minAlt: Int,
            maxAlt: Int
        ): AirportPostion {
            val divisor = 1_000_000

            val latitude = (Random.nextInt(minLat .. maxLat).toFloat() / divisor)
            val longitude = (Random.nextInt(minLng .. maxLng).toFloat() / divisor)
            val altitude = Random.nextInt(minAlt, maxAlt)

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

        override fun generateOne(): Airport {
            val airportName = generateAirportName()

            return Airport(
                id = IdGenerator.setId(),
                name = airportName,
                code = generateAirportCode(),
                position = generateAirportPosition(),
                isVisible = true,
                website = generateRandomWebsite(airportName)
            )
        }

        override fun generate(len: Int): SnapshotStateList<Airport> = SnapshotStateList()
        fun generate(
            size: Int,
            minLat: Int,
            maxLat: Int,
            minLng: Int,
            maxLng: Int,
            minAlt: Int,
            maxAlt: Int
        ): SnapshotStateList<Airport> {
            val airports = SnapshotStateList<Airport>()

            for(i in 0 until size) {
                val airportName = generateAirportName()
                airports.add(
                    Airport(
                        id = IdGenerator.setId(),
                        name = airportName,
                        code = generateAirportCode(),
                        position = generateAirportPosition(
                            minLat = minLat,
                            maxLat = maxLat,
                            minLng = minLng,
                            maxLng = maxLng,
                            minAlt = minAlt,
                            maxAlt = maxAlt
                        ),
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

        private fun generateLatitudeAndLongitude(): Pair<Float, Float> {
            val divisor = 1_000_000

            // 45.660000, 46.660000
            val latMin = 45_660_660
            val latMax = 46_660_000

            // 13.510000, 16.190000
            val lngMin = 13_510_000
            val lngMax = 16_190_000

            val lat = (Random.nextInt(latMin, latMax).toFloat() / divisor)
            val lng = (Random.nextInt(lngMin, lngMax).toFloat() / divisor)

            return Pair(lat, lng)
        }

        private fun generateAltitude(): Int {
            val altMin = 0
            val altMax = 12_000

            return Random.nextInt(altMin, altMax)
        }

        private fun generateSpeed(): Int {
            val speedMin = 0
            // https://www.flyingmag.com/guides/how-fast-do-commerical-planes-fly/
            val speedMax = 1200

            return Random.nextInt(speedMin, speedMax)
        }

        private fun generateDirection(): Int {
            val angleMin = 0
            val angleMax = 360

            return Random.nextInt(angleMin, angleMax)
        }

        private fun generateFlightTrail(len: Int): List<FlightTrail> {
            val trail: MutableList<FlightTrail> = mutableListOf()

            // timestamp of the generated trail
            var ts = Instant.now().epochSecond

            for(i in 0 until len) {
                val (lat, lng) = generateLatitudeAndLongitude()
                val alt = generateAltitude()
                val spd = generateSpeed()
                val hd = generateDirection()

                trail.add(
                    FlightTrail(
                        lat = lat,
                        lng = lng,
                        alt = alt,
                        spd = spd,
                        ts = ts,
                        hd = hd
                    )
                )

                // so that it simulates the 'generation' of the trail every 5 seconds
                ts += 5
            }

            return trail
        }

        override fun generateOne(): Flight {
            // length of the trail
            val trailLen = Random.nextInt(10, 100)

            return Flight(
                id = IdGenerator.setId(),
                identification = generateFlightIdentification(),
                status = generateStatus(),
                owner = generateAirline(),
                airspace = null,
                time = generateTime(),
                trail = generateFlightTrail(trailLen),
                firstTimestamp = Instant.now().epochSecond
            )
        }

        override fun generate(len: Int): SnapshotStateList<Flight> {
            val flights = SnapshotStateList<Flight>()


            for(i in 0 until len) {
                val trailLen = Random.nextInt(10, 100)

                flights.add(
                    Flight(
                        id = IdGenerator.id,
                        identification = generateFlightIdentification(),
                        status = generateStatus(),
                        owner = generateAirline(),
                        airspace = null,
                        time = generateTime(),
                        trail = generateFlightTrail(trailLen),
                        firstTimestamp = Instant.now().epochSecond
                    )
                )
            }

            return flights
        }

        override fun serialize(arr: List<Flight>): String {
            return Json.encodeToString(ListSerializer(Flight.serializer()), arr)
        }
    }
}