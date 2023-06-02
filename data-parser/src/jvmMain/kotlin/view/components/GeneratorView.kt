package view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import data.serialize
import generator.Generator
import generator.model.aircraft.Aircraft
import generator.model.airline.Airline
import generator.model.airport.Airport
import view.COLOR_CARD
import view.COLOR_PRIMARY
import view.DEFAULT_GENERATOR_ROUTE
import view.DEFAULT_PARSER_ROUTE
import view.components.Components.CardText
import view.components.Components.DeleteButton
import view.components.Components.EditButton
import view.components.Components.GenerateButton
import view.components.Components.InputAmountInt
import view.components.Components.InputAmountFloat
import view.components.Components.SendButton

object GeneratorView {

    // (Int, Int, Int, Int) -
    // size of aircrafts,
    // number of images,
    // min size of flight history,
    // max size of flight history strings
    @Composable
    fun GenerateAircrafts(onGenerateAircrafts: (Int, Int, Int, Int) -> Unit) {
        var size by remember { mutableStateOf("0") }
        var imagesSize by remember { mutableStateOf("0") }
        var flightHistoryMin by remember { mutableStateOf("0") }
        var flightHistoryMax by remember { mutableStateOf("0") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            InputAmountInt(
                onChange = { value ->
                    size = value
                },
                label = "Aircrafts"
            )
            InputAmountInt(
                onChange = { value ->
                    imagesSize = value
                },
                label = "Images"
            )
            InputAmountInt(
                onChange = { value ->
                    flightHistoryMin = value
                },
                label = "Min"
            )
            InputAmountInt(
                onChange = { value ->
                    flightHistoryMax = value
                },
                label = "Max"
            )

            GenerateButton(
                onClick = {
                    onGenerateAircrafts(
                        size.toInt(),
                        imagesSize.toInt(),
                        flightHistoryMin.toInt(),
                        flightHistoryMax.toInt()
                    )
                }
            )
        }
    }

    @Composable
    fun GenerateAirlines(onGenerate: (Int) -> Unit) {
        var size by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            InputAmountInt(
                onChange = { value ->
                    size = value
                },
                label = "Airlines"
            )
            GenerateButton(onClick = { onGenerate(size.toInt()) })
        }
    }

    // (Int, Int, Int, Int, Int, Int, Int)
    // number of airports,
    // min lat,
    // max lat,
    // min lng,
    // max lng
    // min alt
    // max alt
    @Composable
    fun GenerateAirports(onGenerate: (Int, Int, Int, Int, Int, Int, Int) -> Unit) {
        var size by remember { mutableStateOf("0") }
        var minLat by remember { mutableStateOf("0")}
        var maxLat by remember { mutableStateOf("0")}
        var minLng by remember { mutableStateOf("0")}
        var maxLng by remember { mutableStateOf("0")}
        var minAlt by remember { mutableStateOf("0")}
        var maxAlt by remember { mutableStateOf("0")}

        val factor = 1_000_000

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            InputAmountInt(
                onChange = { value ->
                    size = value
                },
                label = "Airports"
            )
            InputAmountFloat(
                onChange = { value ->
                    minLat = value
                },
                label = "Min latitude"
            )
            InputAmountFloat(
                onChange = { value ->
                    maxLat = value
                },
                label = "Max latitude"
            )
            InputAmountFloat(
                onChange = { value ->
                    minLng = value
                },
                label = "Min longitude"
            )
            InputAmountFloat(
                onChange = { value ->
                    maxLng = value
                },
                label = "Max longitude"
            )
            InputAmountInt(
                onChange = { value ->
                    minAlt = value
                },
                label = "Min altitude"
            )
            InputAmountInt(
                onChange = { value ->
                    maxAlt = value
                },
                label = "Max altitude"
            )
            GenerateButton(
                onClick = {
                    onGenerate(
                        size.toInt(),
                        (minLat.toFloat() * factor).toInt(),
                        (maxLat.toFloat() * factor).toInt(),
                        (minLng.toFloat() * factor).toInt(),
                        (maxLng.toFloat() * factor).toInt(),
                        minAlt.toInt(),
                        maxAlt.toInt()
                    )
                }
            )
        }
    }

    @Composable
    fun RenderAircraftData(
        aircraft: Aircraft,
        isEditing: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = aircraft.model.text,
                weight = 0.33f,
                label = "Model",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.model.text = it
                }
            )
            CardText(
                text = aircraft.model.code,
                weight = 0.33f,
                label = "Code",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.model.code = it
                }
            )
            CardText(
                text = aircraft.registration,
                weight = 0.33f,
                label = "Registration",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.registration = it
                }
            )
        }
    }

    @Composable
    fun RenderAirlineData(
        airline: Airline,
        isEditing: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airline.name,
                weight = 0.33f,
                label = "Name",
                isEditing = isEditing,
                onTextChange = {
                    airline.name = it
                }
            )
            CardText(
                text = airline.short,
                weight = 0.33f,
                label = "Short name",
                isEditing = isEditing,
                onTextChange = {
                    airline.short = it
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airline.code.iata,
                weight = 0.33f,
                label = "IATA",
                isEditing = isEditing,
                onTextChange = {
                    airline.code.iata = it
                }
            )
            CardText(
                text = airline.code.icao,
                weight = 0.33f,
                label = "ICAO",
                isEditing = isEditing,
                onTextChange = {
                    airline.code.icao = it
                }
            )
            CardText(
                text = airline.url,
                weight = 0.33f,
                label = "URL",
                isEditing = isEditing,
                onTextChange = {
                    airline.url = it
                }
            )
        }
    }

    @Composable
    fun RenderAirportData(
        airport: Airport,
        isEditing: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airport.name,
                weight = 0.33f,
                label = "Name",
                isEditing = isEditing,
                onTextChange = {
                    airport.name = it
                }
            )
            CardText(
                text = airport.code.iata,
                weight = 0.33f,
                label = "IATA",
                isEditing = isEditing,
                onTextChange = {
                    airport.code.iata = it
                }
            )
            CardText(
                text = airport.code.icao,
                weight = 0.33f,
                label = "ICAO",
                isEditing = isEditing,
                onTextChange = {
                    airport.code.iata = it
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airport.position.country.name,
                weight = 0.33f,
                label = "Country",
                isEditing = isEditing,
                onTextChange = {
                    airport.position.country.name = it
                }
            )
            CardText(
                text = airport.position.country.code,
                weight = 0.33f,
                label = "Country code",
                isEditing = isEditing,
                onTextChange = {
                    airport.code.iata = it
                }
            )
            CardText(
                text = airport.position.region.city,
                weight = 0.33f,
                label = "City",
                isEditing = isEditing,
                onTextChange = {
                    airport.position.region.city = it
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airport.position.latitude.toString(),
                weight = 0.33f,
                label = "Latitude",
                isEditing = isEditing,
                onTextChange = {
                    airport.position.latitude = it.toFloat()
                }
            )
            CardText(
                text = airport.position.longitude.toString(),
                weight = 0.33f,
                label = "Longitude",
                isEditing = isEditing,
                onTextChange = {
                    airport.position.longitude = it.toFloat()
                }
            )
            CardText(
                text = airport.position.altitude.toString(),
                weight = 0.33f,
                label = "Altitude",
                isEditing = isEditing,
                onTextChange = {
                    airport.position.altitude = it.toInt()
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = airport.website,
                weight = 0.33f,
                label = "Website",
                isEditing = isEditing,
                onTextChange = {
                    airport.website = it
                }
            )
        }
    }

    @Composable
    fun AircraftCard(
        aircraft: Aircraft,
        onDelete: (Aircraft) -> Unit
    ) {
        var isEditing by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Color(COLOR_PRIMARY),
                    shape = RoundedCornerShape(5.dp)
                )
                .background(color = Color(COLOR_CARD))
                .fillMaxWidth(0.67f)
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .weight(0.15f)
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
            ) {
                RenderAircraftData(
                    aircraft = aircraft,
                    isEditing = isEditing
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.25f)
            ) {
                EditButton(onClick = { isEditing = !isEditing })
                DeleteButton(onClick = { onDelete(aircraft) })
            }
        }
    }

    @Composable
    fun AirlineCard(
        airline: Airline,
        onDelete: (Airline) -> Unit
    ) {
        var isEditing by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Color(COLOR_PRIMARY),
                    shape = RoundedCornerShape(5.dp)
                )
                .background(color = Color(COLOR_CARD))
                .fillMaxWidth(0.67f)
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .weight(0.15f)
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
            ) {
                RenderAirlineData(
                    airline = airline,
                    isEditing = isEditing
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.25f)
            ) {
                EditButton(onClick = { isEditing = !isEditing })
                DeleteButton(onClick = { onDelete(airline) })
            }
        }
    }

    @Composable
    fun AirportCard(
        airport: Airport,
        onDelete: (Airport) -> Unit
    ) {
        var isEditing by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Color(COLOR_PRIMARY),
                    shape = RoundedCornerShape(5.dp)
                )
                .background(color = Color(COLOR_CARD))
                .fillMaxWidth(0.67f)
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .weight(0.15f)
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
            ) {
                RenderAirportData(
                    airport = airport,
                    isEditing = isEditing
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.25f)
            ) {
                EditButton(onClick = { isEditing = !isEditing })
                DeleteButton(onClick = { onDelete(airport) })
            }
        }
    }

    @Composable
    fun RenderAircrafts() {
        var aircrafts: SnapshotStateList<Aircraft> = remember { mutableStateListOf() }

        GenerateAircrafts(
            onGenerateAircrafts = { aircraftsLen, imagesLen, flightHistoryMin, flightHistoryMax ->
                aircrafts.clear()
                aircrafts.addAll(
                    Generator.AircraftGenerator.generate(
                        len = aircraftsLen,
                        images = imagesLen,
                        flightHistoryMin = flightHistoryMin,
                        flightHistoryMax = flightHistoryMax
                    )
                )
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(aircrafts, key = { aircraft -> aircraft.id }) { aircraft ->
                        AircraftCard(aircraft, onDelete = {
                            aircrafts.remove(aircraft)
                        })
                    }
                }
            }
            if(!aircrafts.isEmpty()) {
                SendButton(
                    onClick = {
                        println(Generator.AircraftGenerator.serialize(aircrafts))
                    }
                )
            }
        }
    }

    @Composable
    fun RenderAirlines() {
        var airlines: SnapshotStateList<Airline> = remember { mutableStateListOf() }

        GenerateAirlines(onGenerate = { len ->
            airlines.clear()
            airlines.addAll(Generator.AirlineGenerator.generate(len))
        })

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(airlines, key = { airline -> airline.id }) { airline ->
                        AirlineCard(airline, onDelete = {
                            airlines.remove(airline)
                        })
                    }
                }
            }
            if(!airlines.isEmpty()) {
                SendButton(
                    onClick = {
                        println(Generator.AirlineGenerator.serialize(airlines))
                    }
                )
            }
        }
    }

    @Composable
    fun RenderAirports() {
        var airports: SnapshotStateList<Airport> = remember { mutableStateListOf() }

        GenerateAirports(
            onGenerate = {
                size, minLat, maxLat, minLng, maxLng, minAlt, maxAlt ->
                    airports.clear()
                    airports.addAll(
                        Generator.AirportGenerator.generate(
                            size = size,
                            minLat = minLat,
                            maxLat = maxLat,
                            minLng = minLng,
                            maxLng = maxLng,
                            minAlt = minAlt,
                            maxAlt = maxAlt
                        )
                    )
            }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(airports, key = { airport -> airport.id }) { airport ->
                        AirportCard(airport, onDelete = {
                            airports.remove(airport)
                        })
                    }
                }
            }
            if(!airports.isEmpty()) {
                SendButton(
                    onClick = {
                        println(Generator.AirportGenerator.serialize(airports))
                    }
                )
            }
        }
    }

    @Composable
    fun GeneratorNavigation(
        elements: List<GeneratorNavigation>,
        currentRoute: String,
        clickedRoute: (String) -> Unit
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(8.dp)
                .drawBehind {
                    // https://medium.com/@banmarkovic/jetpack-compose-bottom-border-8f1662c2aa84
                    drawLine(
                        color = Color(COLOR_PRIMARY),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .fillMaxWidth()
        ) {
            items(elements) { item ->
                Button(
                    onClick = { clickedRoute(item.route) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        text = item.title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraLight,
                        fontSize = 20.sp,
                        color = Color(COLOR_PRIMARY)
                    )
                }
            }
        }
    }

    @Composable
    fun GeneratorScreen(
    ) {
        var currentRoute by remember { mutableStateOf(DEFAULT_GENERATOR_ROUTE) }
        val (
            aircraftsRoute,
            airlinesRoute,
            airportsRoute,
            flightsRoute
        ) = GeneratorNavigation.getAllRoutes()

        Column {
            GeneratorNavigation(
                elements = GeneratorNavigation.getAllElements(),
                currentRoute = DEFAULT_GENERATOR_ROUTE,
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
            when(currentRoute) {
                aircraftsRoute -> RenderAircrafts()
                airlinesRoute -> RenderAirlines()
                airportsRoute -> RenderAirports()
                flightsRoute -> println("Flights")
            }
        }
    }
}