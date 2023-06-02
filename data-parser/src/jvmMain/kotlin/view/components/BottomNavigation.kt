package view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.vector.ImageVector

open class TopNavigation(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Parser: TopNavigation(
        route = "parser",
        title = "PARSER",
        icon = Icons.Rounded.Create
    )

    object Generator: TopNavigation(
        route = "generator",
        title = "GENERATOR",
        icon = Icons.Rounded.Build
    )

    companion object {
        fun getAllElements(): List<TopNavigation> {
            return listOf(
                Parser,
                Generator
            )
        }

        fun getAllRoutes(): List<String> {
            return listOf(
                Parser.route,
                Generator.route
            )
        }
    }
}