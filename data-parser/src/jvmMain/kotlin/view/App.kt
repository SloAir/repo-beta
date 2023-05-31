package view

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.scrapeData
import view.components.*

const val DEFAULT_NAVIGATION_ROUTE = "parser"
const val DEFAULT_PARSER_ROUTE = "arrivals"
const val DEFAULT_GENERATOR_ROUTE = "aircrafts"

// function displays a bottom navigation bar menu
// for parser and generator part of the app
@Composable
fun NavigationBar(
    elements: List<BottomNavigation>,
    currentRoute: String,
    clickedRoute: (String) -> Unit
) {
    val height = 48.dp

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
    ) {
        elements.forEachIndexed { index, item ->
            Button(
                onClick = { clickedRoute(item.route) },
                shape = RectangleShape,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight(),
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
fun MainScreen() {
    var currentRoute by remember { mutableStateOf(DEFAULT_NAVIGATION_ROUTE) }
    val (parserRoute, generatorRoute) = BottomNavigation.getAllRoutes()
    val (arrivals, departures) = scrapeData()

    Scaffold(
        bottomBar = {
            NavigationBar(
                elements = BottomNavigation.getAllElements(),
                currentRoute = DEFAULT_PARSER_ROUTE,
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
        }
    ) {
        when(currentRoute) {
            parserRoute -> ParserView.ParserScreen(arrivals, departures)
            generatorRoute -> GeneratorView.GeneratorScreen()
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Data parser"
    ) {
        MainScreen()
    }
}
