package view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

open class ParserNavigation(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Arrivals: ParserNavigation(
        route = "arrivals",
        title = "ARRIVALS",
        icon = Icons.Rounded.Add
    )

    object Departures: ParserNavigation(
        route = "departures",
        title = "DEPARTURES",
        icon = Icons.Rounded.Refresh
    )

    companion object {
        fun getAllElements(): List<ParserNavigation> {
            return listOf(
                Arrivals,
                Departures
            )
        }

        fun getAllRoutes(): List<String> {
            return listOf(
                Arrivals.route,
                Departures.route
            )
        }
    }
}