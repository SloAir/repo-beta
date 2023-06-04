package view

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import data.scrapeData
import io.github.cdimascio.dotenv.dotenv
import view.components.*


val dotenv = dotenv()

val SERVER_URL = dotenv["SERVER_URL"]
val SERVER_PORT = dotenv["SERVER_PORT"]


const val COLOR_PRIMARY = 0XFFDDE6ED
const val COLOR_BACKGROUND = 0xFF27374D
const val COLOR_CARD = 0xFF526D82
const val COLOR_BUTTON = 0XFF526D82

const val DEFAULT_NAVIGATION_ROUTE = "parser"
const val DEFAULT_PARSER_ROUTE = "arrivals"
const val DEFAULT_GENERATOR_ROUTE = "aircrafts"

// function displays a bottom navigation bar menu
// for parser and generator part of the app
@Composable
fun NavigationBar(
    elements: List<TopNavigation>,
    currentRoute: String,
    clickedRoute: (String) -> Unit
) {
    val height = 50.dp

    BottomNavigation(
        modifier = Modifier
            .height(height)
    ) {
        elements.forEach { item ->
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()

            Button(
                onClick = { clickedRoute(item.route) },
                shape = RectangleShape,
                modifier = Modifier
                    .hoverable(interactionSource = interactionSource)
                    .weight(1f)
                    .fillMaxSize(),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if(isHovered) Color(COLOR_BACKGROUND) else Color(COLOR_BUTTON),
                    contentColor = Color(COLOR_PRIMARY)
                )
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
                Spacer(
                    modifier = Modifier
                        .width(16.dp)
                )
                Text(
                    text = item.title,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf(DEFAULT_NAVIGATION_ROUTE) }
    val (parserRoute, generatorRoute) = TopNavigation.getAllRoutes()

    val arrivals = remember { mutableStateOf(scrapeData().first) }
    val departures = remember { mutableStateOf(scrapeData().second) }

    Scaffold(
        topBar = {
            NavigationBar(
                elements = TopNavigation.getAllElements(),
                currentRoute = DEFAULT_NAVIGATION_ROUTE,
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
        },
        backgroundColor = Color(COLOR_BACKGROUND)
    ) {
        when(currentRoute) {
            parserRoute ->
                ParserView.ParserScreen(
                    arrivals = arrivals.value,
                    departures = departures.value,
                )
            generatorRoute ->
                GeneratorView.GeneratorScreen()
        }
    }
}

fun main() = application {

    val windowState = rememberWindowState(
        width = 1900.dp,
        height = 1000.dp
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Data parser",
        state = windowState
    ) {
        MainScreen()
    }
}
