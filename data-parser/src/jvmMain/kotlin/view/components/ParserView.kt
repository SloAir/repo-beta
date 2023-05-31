package view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import data.model.Arrival
import data.model.Departure
import data.model.Flight
import data.serialize
import view.*


object ParserView {
    // function modifies the object's variable values
    // according to the changes in the text box
    @Composable
    fun ModifyText(
        // object's string value
        str: String,
        onTextChange: (String) -> Unit
    ) {
        var text by remember { mutableStateOf(TextFieldValue(str)) }
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onTextChange(newText.text)
            }
        )
    }

    // function 'verifies' the data and sends the verified/modified data to the
    // API endpoint, where it will be updated or saved into the database
    @Composable
    fun VerifyData(flights: List<Flight>) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Button(
                onClick = {
                    serialize(flights)
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send"
                )
            }
        }
    }

    // function displays the flight's date, planned and expected landing/departure time
    // in a text box and saves the modified data to the object
    @Composable
    fun FlightDateTime(
        flight: Flight,
        onDateChange: (String) -> Unit,
        onPlannedChange: (String) -> Unit,
        onExpectedChange: (String) -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModifyText(
                str = flight.date,
                onTextChange = onDateChange
            )
            ModifyText(
                str = flight.planned,
                onTextChange = onPlannedChange
            )
            ModifyText(
                str = flight.expected,
                onTextChange = onExpectedChange,
            )
        }
    }

    // function displays the row of the destination variable
    // in a text box and saves the modified data to the object
    @Composable
    fun FlightDestination(
        flight: Flight,
        onDestinationChange: (String) -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            ModifyText(
                str = flight.destination,
                onTextChange = onDestinationChange
            )
        }
    }

    // function displays the flight number and status in a row,
    // in a text box and saves the modified data to the object
    @Composable
    fun FlightMetadata(
        flight: Flight,
        onFlightNumberChange: (String) -> Unit,
        onFlightStatusChange: (String) -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ModifyText(
                str = flight.flightNumber,
                onTextChange = onFlightNumberChange
            )
            ModifyText(
                str = flight.flightStatus,
                onTextChange = onFlightStatusChange
            )
        }
    }

    // function displays the flight's metadata in a row,
    // in a text box and saves the modified data to the object
    @Composable
    fun DepartureMetadata(
        flight: Departure,
        onExitTagChange: (String) -> Unit,
        onCheckInChange: (String) -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ModifyText(
                str = flight.exitTag,
                onTextChange = onExitTagChange
            )
            ModifyText(
                str = flight.checkIn,
                onTextChange = onCheckInChange
            )
        }
    }

    // function renders the modifiable data of a single flight
    @Composable
    fun FlightCard(flight: Flight) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(20.dp)
                .border(BorderStroke(1.dp, SolidColor(Color.Black)))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                FlightDateTime(
                    flight,
                    onDateChange = { newDate -> flight.date = newDate },
                    onPlannedChange = { newPlanned -> flight.planned = newPlanned },
                    onExpectedChange = { newExpected -> flight.expected = newExpected }
                )
                FlightDestination(
                    flight,
                    onDestinationChange = { newDestination -> flight.destination = newDestination }
                )
                FlightMetadata(
                    flight,
                    onFlightNumberChange = { newFlightNumber -> flight.flightNumber = newFlightNumber },
                    onFlightStatusChange = { newFlightStatus -> flight.flightStatus = newFlightStatus }
                )
                if(flight is Departure) {
                    DepartureMetadata(
                        flight,
                        onExitTagChange = { newExitTag -> flight.exitTag = newExitTag },
                        onCheckInChange = { newCheckIn -> flight.checkIn = newCheckIn }
                    )
                }
            }
        }
    }

    // function renders all of the arrivals as a column
    @Composable
    fun RenderArrivals(arrivals: List<Arrival>) {
        LazyColumn {
            items(arrivals) { item ->
                FlightCard(item)
            }
        }
    }

    // function renders all of the departures as a column
    @Composable
    fun RenderDepartures(departures: List<Departure>) {
        LazyColumn {
            items(departures) { item ->
                FlightCard(item)
            }
        }
    }

    // function displays the navigation bar of the parser screen
    @Composable
    fun ParserNavigation(
        elements: List<ParserNavigation>,
        currentRoute: String,
        clickedRoute: (String) -> Unit
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            elements.forEach { item ->
                Button(
                    onClick = { clickedRoute(item.route) },
                    shape = RectangleShape,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    elevation = ButtonDefaults.elevation(
                        0.dp,
                        0.dp,
                        0.dp,
                        0.dp,
                        0.dp
                    )
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                    Text(
                        text = item.title
                    )
                }
            }
        }
    }

    @Composable
    fun ParserScreen(
        arrivals: List<Arrival>,
        departures: List<Departure>
    ) {
        var currentRoute by remember { mutableStateOf(DEFAULT_PARSER_ROUTE) }
        val (arrivalsRoute, departuresRoute) = ParserNavigation.getAllRoutes()

        Scaffold(
            topBar = {
                ParserNavigation(
                    elements = ParserNavigation.getAllElements(),
                    currentRoute = DEFAULT_PARSER_ROUTE,
                    clickedRoute = { route ->
                        currentRoute = route
                    }
                )
            },
            bottomBar = {
                when(currentRoute) {
                    arrivalsRoute -> VerifyData(arrivals)
                    departuresRoute -> VerifyData(departures)
                }
            },
            modifier = Modifier
                .padding(bottom = 45.dp)
        ) {
            when(currentRoute) {
                arrivalsRoute -> RenderArrivals(arrivals)
                departuresRoute -> RenderDepartures(departures)
            }
        }
    }
}