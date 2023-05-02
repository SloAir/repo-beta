import data.model.WeatherData
import it.skrape.core.*
import it.skrape.fetcher.*

fun main() {
    try {
        val scrapedData = skrape(HttpFetcher) {
            request {
                url = "https://meteo.arso.gov.si/uploads/probase/www/observ/surface/text/sl/observationAms_si_latest.html"
            }

            response {
                htmlDocument {
                    val locations = mutableListOf<String>()
                    findAll("td.meteoSI-th").forEach {
                        locations.add(it.text)
                    }
                    val weatherData = mutableListOf<WeatherData>()
                    findAll("table.meteoSI-table > tbody > tr").forEach {
                        val location = it.findFirst("td:nth-child(1)").text
                        val temperature = it.findFirst("td:nth-child(3)").text
                        val humidity = it.findFirst("td:nth-child(4)").text
                        var rainfall = it.findFirst("td:nth-child(9)").text
                        var snowfall = it.findFirst("td:nth-child(11)").text
                        if (snowfall==""){
                            snowfall="Ni podatka"
                        }
                        if (rainfall==""){
                            rainfall="Ni podatka"
                        }
                        weatherData.add(WeatherData(location, temperature, humidity, rainfall, snowfall))
                    }
                    weatherData
                }
            }

        }
        println("ALL LOCATIONS and their TEMPERATURE, HUMIDITY, RAINFLL, SNOWFALL")
        println("---------------------------------------------------------------------------------------------------------------------------------")
        println("Ime".padEnd(42) + "TEMP".padEnd(5) + "HUM".padEnd(9) + "PAD".padEnd(13) + "SNEG")
        scrapedData.forEach { weather ->
            println("${weather.location.padEnd(40)} | ${weather.temperature.padEnd(2)} | ${weather.humidity.padEnd(2)} | ${weather.rainfall.padEnd(10)} | ${weather.snowfall.padEnd(2)}")
        }
        println("")
        println("ALL LOCATIONS where there is no RAINFALL")
        println("---------------------------------------------------------------------------------------------------------------------------------")
        scrapedData.filter { it.rainfall == "0" }.forEach { weather ->
            println("${weather.location.padEnd(40)} | ${weather.temperature.padEnd(2)} | ${weather.humidity.padEnd(2)} | ${weather.rainfall.padEnd(10)} | ${weather.snowfall.padEnd(2)}")

        }

        println("---------------------------------------------------------------------------------------------------------------------------------")
        println("LOCATIONS with airports")
        scrapedData.filter { it.location == "Ljubljana" || it.location=="Maribor" }.forEach { weather ->
            println("${weather.location.padEnd(40)} | ${weather.temperature.padEnd(2)} | ${weather.humidity.padEnd(2)} | ${weather.rainfall.padEnd(10)} | ${weather.snowfall.padEnd(2)}")

        }

    } catch (ex : Exception) {
        println(ex.message)
    }
}
