package view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.List
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import generator.Generator
import generator.model.aircraft.Aircraft
import view.COLOR_CARD
import view.COLOR_PRIMARY
import view.DEFAULT_GENERATOR_ROUTE
import view.DEFAULT_PARSER_ROUTE
import view.components.Components.CardText
import view.components.Components.DeleteButton
import view.components.Components.EditButton
import view.components.Components.GenerateButton
import view.components.Components.InputAmount

object GeneratorView {
    @Composable
    fun GenerateAircrafts(onGenerate: (Int) -> Unit) {
        var len by remember { mutableStateOf("") }
        var aircrafts: SnapshotStateList<Aircraft> = remember { mutableStateListOf() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            InputAmount(onChange = { value ->
                len = value
            })
            GenerateButton(onClick = { onGenerate(len.toInt()) })
        }
    }

    @Composable
    fun RenderAircraftData(
        aircraft: Aircraft,
        isEditing: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {
            CardText(
                text = aircraft.model.text,
                weight = 0.33f,
                label = "Model",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.model.text = it
                }
            )
            CardText(
                text = aircraft.model.code,
                weight = 0.33f,
                label = "Code",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.model.code = it
                }
            )
            CardText(
                text = aircraft.registration,
                weight = 0.33f,
                label = "Registration",
                isEditing = isEditing,
                onTextChange = {
                    aircraft.registration = it
                }
            )
        }
    }

    @Composable
    fun AircraftCard(
        aircraft: Aircraft,
        onDelete: (Aircraft) -> Unit
    ) {
        var isEditing by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    color = Color(COLOR_PRIMARY),
                    shape = RoundedCornerShape(5.dp)
                )
                .background(color = Color(COLOR_CARD))
                .fillMaxWidth(0.67f)
        ) {
            Icon(
                imageVector = Icons.Rounded.List,
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .weight(0.15f)
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
            ) {
                RenderAircraftData(
                    aircraft = aircraft,
                    isEditing = isEditing
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(0.25f)
            ) {
                EditButton(onClick = { isEditing = !isEditing })
                DeleteButton(onClick = { onDelete(aircraft) })
            }
        }
    }

    @Composable
    fun RenderAircrafts() {
        var aircrafts: SnapshotStateList<Aircraft> = remember { mutableStateListOf() }

        GenerateAircrafts(onGenerate = { len ->
            aircrafts.clear()
            aircrafts.addAll(Generator.AircraftGenerator.generate(len))
        })

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(aircrafts, key = { aircraft -> aircraft.id }) { aircraft ->
                AircraftCard(aircraft, onDelete = {
                    aircrafts.remove(aircraft)
                })
            }
        }
    }

    @Composable
    fun GeneratorNavigation(
        elements: List<GeneratorNavigation>,
        currentRoute: String,
        clickedRoute: (String) -> Unit
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(8.dp)
                .drawBehind {
                    // https://medium.com/@banmarkovic/jetpack-compose-bottom-border-8f1662c2aa84
                    drawLine(
                        color = Color(COLOR_PRIMARY),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .fillMaxWidth()
        ) {
            items(elements) { item ->
                Button(
                    onClick = { clickedRoute(item.route) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        text = item.title,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraLight,
                        fontSize = 20.sp,
                        color = Color(COLOR_PRIMARY)
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

        Column {
            GeneratorNavigation(
                elements = GeneratorNavigation.getAllElements(),
                currentRoute = DEFAULT_GENERATOR_ROUTE,
                clickedRoute = { route ->
                    currentRoute = route
                }
            )
            when(currentRoute) {
                aircraftsRoute -> RenderAircrafts()
                airlinesRoute -> println("Airlines")
                airportsRoute -> println("Airports")
                flightsRoute -> println("Flights")
            }
        }
    }
}