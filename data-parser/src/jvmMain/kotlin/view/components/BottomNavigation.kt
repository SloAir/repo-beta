package view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.vector.ImageVector

open class BottomNavigation(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Parser: BottomNavigation(
        route = "parser",
        title = "Parser",
        icon = Icons.Rounded.List   // Placeholder
    )

    object Generator: BottomNavigation(
        route = "generator",
        title = "Generator",
        icon = Icons.Rounded.Call   // Placeholder
    )

    companion object {
        fun getAllElements(): List<BottomNavigation> {
            return listOf<BottomNavigation>(Parser, Generator)
        }
    }
}