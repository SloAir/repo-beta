package view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.DateParser
import data.Request
import data.checkFlightType
import data.model.Arrival
import data.model.Departure
import data.model.Flight
import data.serialize
import kotlinx.serialization.json.JsonObject
import view.*
import view.components.Components.CardText
import view.components.Components.DeleteButton
import view.components.Components.EditButton
import view.components.Components.GenericText
import view.components.Components.SendButton

object ParserView {
    @Composable
    fun RenderData(
        flight: Flight,
        isEditing: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = flight.date,
                weight = 0.33f,
                label = "Date",
                isEditing = isEditing,
                onTextChange = {
                    flight.date = it
                }
            )
            CardText(
                text = flight.expected,
                weight = 0.33f,
                label = "Expected",
                isEditing = isEditing,
                onTextChange = {
                    flight.expected = it
                }
            )
            CardText(
                text = flight.planned,
                weight = 0.33f,
                label = "Planned",
                isEditing = isEditing,
                onTextChange = {
                    flight.planned = it
                }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = flight.destination,
                weight = 0.33f,
                label = "Destination",
                isEditing = isEditing,
                onTextChange = {
                    flight.destination = it
                }
            )
            CardText(
                text = flight.flightNumber,
                weight = 0.33f,
                label = "Flight number",
                isEditing = isEditing,
                onTextChange = {
                    flight.flightNumber = it
                }
            )
            CardText(
                text = flight.flightStatus,
                weight = 0.33f,
                label = "Status",
                isEditing = isEditing,
                onTextChange = {
                    flight.flightStatus = it
                }
            )
        }

        if(flight is Departure) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(10.dp)
            ) {
                CardText(
                    text = flight.checkIn,
                    weight = 0.33f,
                    label = "Check-in",
                    isEditing = isEditing,
                    onTextChange = {
                        flight.checkIn = it
                    }
                )
                CardText(
                    text = flight.exitTag,
                    weight = 0.33f,
                    label = "Exit tag",
                    isEditing = isEditing,
                    onTextChange = {
                        flight.exitTag = it
                    }
                )
            }
        }
    }

    @Composable
    fun FlightCard(flight: Flight, onDelete: (Flight) -> Unit) {
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
                .fillMaxWidth(0.85f)
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
                RenderData(
                    flight = flight,
                    isEditing = isEditing
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.25f)
            ) {
                EditButton(onClick = { isEditing = !isEditing })
                DeleteButton(onClick = { onDelete(flight) })
            }
        }
    }

    @Composable
    fun RenderFlights(flights: SnapshotStateList<Flight>) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        )  {
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
                    items(flights, key = { flight -> flight.id }) { flight ->
                        FlightCard(flight, onDelete = {
                            flights.remove(flight)
                        })
                    }
                }
            }
            SendButton(
                onClick = {
                    val date = DateParser.parseDate(flights[0].date)
                    val json = serialize(flights, date)
                    if(checkFlightType<Arrival>(flights)) {
                        Request.sendRequest(
                            url = "http://127.0.0.1:8000/api/arrivals/post/",
                            method = "POST",
                            headers = mapOf(
                                "Content-Type" to "application/json"
                            ),
                            body = json
                        )
                    }
                    else if(checkFlightType<Departure>(flights)) {
                        Request.sendRequest(
                            url = "http://127.0.0.1:8000/api/departures/post/",
                            method = "POST",
                            headers = mapOf(
                                "Content-Type" to "application/json"
                            ),
                            body = json
                        )
                    }
                    flights.clear()
                }
            )
        }
    }

    @Composable
    fun ParserNavigation(
        elements: List<ParserNavigation>,
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
    fun ParserScreen(
        arrivals: SnapshotStateList<Flight>,
        departures: SnapshotStateList<Flight>
    ) {
        var currentRoute by remember { mutableStateOf(DEFAULT_PARSER_ROUTE) }
        val (arrivalsRoute, departuresRoute) = ParserNavigation.getAllRoutes()

        Column {
            ParserNavigation(
                elements = ParserNavigation.getAllElements(),
                currentRoute = DEFAULT_PARSER_ROUTE,
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
            when(currentRoute) {
                arrivalsRoute -> RenderFlights(arrivals)
                departuresRoute -> RenderFlights(departures)
            }
        }
    }
}