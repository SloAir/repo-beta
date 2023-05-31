package view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import view.DEFAULT_GENERATOR_ROUTE

object GeneratorView {
    @Composable
    fun GeneratorNavigation(
        elements: List<GeneratorNavigation>,
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
    fun GeneratorScreen(
    ) {
        var currentRoute by remember { mutableStateOf(DEFAULT_GENERATOR_ROUTE) }
        val (
            aircraftsRoute,
            airlinesRoute,
            airportsRoute,
            flightsRoute
        ) = GeneratorNavigation.getAllRoutes()

        Scaffold(
            topBar = {
                GeneratorNavigation(
                    elements = GeneratorNavigation.getAllElements(),
                    currentRoute = DEFAULT_GENERATOR_ROUTE,
                    clickedRoute = { route ->
                        currentRoute = route
                    }
                )
            },
            content = {
                when(currentRoute) {
                    aircraftsRoute -> println("Aircrafts")
                    airlinesRoute -> println("Airlines")
                    airportsRoute -> println("Airports")
                    flightsRoute -> println("Flights")
                }
            }
        )
    }
}