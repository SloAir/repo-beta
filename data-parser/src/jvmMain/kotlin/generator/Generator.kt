package generator

import generator.model.Code
import generator.model.aircraft.*
import generator.model.airline.*
import generator.model.airport.*
import generator.model.flight.*
import generator.model.readFromFile
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
    }

    object FlightGenerator: IGenerator<Flight> {
        override fun generate(count: Int): List<Flight> {
            TODO("Not yet implemented")
        }
    }
}

fun main() {
    // Generator.Airport.generate()
    val aircrafts = Generator.AircraftGenerator.generate(10)
    val airlines = Generator.AirlineGenerator.generate(10)
    val airports = Generator.AirportGenerator.generate(10)

    // aircrafts.forEach { aircraft ->
    //     println(aircraft)
    // }

    airlines.forEach { airline ->
        println()
    }

    // airports.forEach { airport ->
    //     println(airport)
    // }
}