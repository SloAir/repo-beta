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
        title = "Arrivals",
        icon = Icons.Rounded.Add
    )

    object Departures: ParserNavigation(
        route = "departures",
        title = "Departures",
        icon = Icons.Rounded.Refresh
    )

    companion object {
        fun getAllElements(): List<ParserNavigation> {
            return listOf<ParserNavigation>(Arrivals, Departures)
        }
    }
}