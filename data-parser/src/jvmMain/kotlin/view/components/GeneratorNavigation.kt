package view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.ui.graphics.vector.ImageVector

open class GeneratorNavigation(
    val route: String,
    val title: String,
    val icon: ImageVector
)  {
    object Aircrafts: GeneratorNavigation(
        route = "aircrafts",
        title = "Aircrafts",
        icon = Icons.Rounded.Add
    )

    object Airlines: GeneratorNavigation(
        route = "airlines",
        title = "Airlines",
        icon = Icons.Rounded.Add
    )

    object Airports: GeneratorNavigation(
        route = "airports",
        title = "Airports",
        icon = Icons.Rounded.Add
    )

    object Flights: GeneratorNavigation(
        route = "flights",
        title = "Flights",
        icon = Icons.Rounded.Add
    )

    companion object {
        fun getAllElements(): List<GeneratorNavigation> {
            return listOf(
                Aircrafts,
                Airlines,
                Airports,
                Flights
            )
        }

        fun getAllRoutes(): List<String> {
            return listOf(
                Aircrafts.route,
                Airlines.route,
                Airports.route,
                Flights.route
            )
        }
    }
}