package view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.getArrivals
import data.getDepartures
import data.model.Arrival
import data.model.Departure
import data.scrapeData
import org.jetbrains.skia.impl.Log
import view.components.BottomNavigation
import view.components.ParserNavigation
import kotlin.math.exp

// function displays a bottom navigation bar menu
// for parser and generator part of the app
@Composable
fun NavigationBar(
    elements: List<BottomNavigation>,
    currentRoute: String,
    clickedRoute: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
    ) {
        elements.forEach { item ->
            Button(
                onClick = { clickedRoute(item.route) },
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
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

// function displays the flight's date, planned and expected landing/departure time
// in a text box,
@Composable
fun FlightDateTime(
    arrival: Arrival,
    onDateChange: (String) -> Unit,
    onPlannedChange: (String) -> Unit,
    onExpectedChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ModifyText(
            str = arrival.date,
            onTextChange = onDateChange
        )
        ModifyText(
            str = arrival.planned,
            onTextChange = onPlannedChange
        )
        ModifyText(
            str = arrival.expected,
            onTextChange = onExpectedChange
        )
    }
}

@Composable
fun ArrivalCard(arrival: Arrival) {
    Row {
        Icon(
            modifier = Modifier
                .fillMaxWidth(0.2f),
            imageVector = Icons.Rounded.Done,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            FlightDateTime(
                arrival,
                onDateChange = { newDate -> arrival.date = newDate },
                onPlannedChange = { newPlanned -> arrival.planned = newPlanned },
                onExpectedChange = { newExpected -> arrival.expected = newExpected }
            )
        }
    }
}

@Composable
fun ArrivalsList(arrivals: List<Arrival>) {
    LazyColumn {
        items(arrivals) { item ->
            ArrivalCard(item)
        }
    }
}

@Composable
fun ParserNavigation(
    elements: List<ParserNavigation>,
    currentRoute: String,
    clickedRoute: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        elements.forEach { item ->
            Button(
                onClick = { clickedRoute(item.route) },
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
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
    var currentRoute by remember { mutableStateOf("arrivals") }

    Scaffold(
        topBar = {
            view.ParserNavigation(
                elements = ParserNavigation.getAllElements(),
                currentRoute = "arrivals",
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
        },
        content = {
            when(currentRoute) {
                "arrivals" -> ArrivalsList(arrivals)
            }
        }
    )
}

@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf("parser") }
    val (arrivals, departures) = scrapeData()

    Scaffold(
        content = {
            when(currentRoute) {
                "parser" -> ParserScreen(arrivals, departures)
                "generator" -> Text(text = "FUCK OFF")
            }
        },
        bottomBar = {
            NavigationBar(
                elements = BottomNavigation.getAllElements(),
                currentRoute = "parser",
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
        }
    )
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Data parser"
    ) {
        MainScreen()
    }
}
