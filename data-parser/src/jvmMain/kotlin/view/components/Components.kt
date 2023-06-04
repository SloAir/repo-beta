package view.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Send
import androidx.compose.runtime.*
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
import view.COLOR_BACKGROUND
import view.COLOR_BUTTON
import view.COLOR_PRIMARY

object Components {
    @Composable
    fun GenericText(text: String) {
        Text(
            text = text,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraLight,
            fontSize = 16.sp,
            color = Color(COLOR_PRIMARY)
        )
    }

    @Composable
    fun InputAmountInt(
        onChange: (String) -> Unit,
        label: String
    ) {
        var text by remember { mutableStateOf(TextFieldValue("")) }

        OutlinedTextField(
            value = text,
            onValueChange = {
                if(it.text.matches(Regex("\\d+"))) {
                    text = it
                    onChange(it.text)
                }
            },
            label = { Text(text = label) },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(COLOR_PRIMARY),
                focusedIndicatorColor = Color(COLOR_PRIMARY),
                focusedLabelColor = Color(COLOR_PRIMARY),
                unfocusedLabelColor = Color(COLOR_PRIMARY),
                cursorColor = Color(COLOR_PRIMARY)
            )
        )
    }

    @Composable
    fun InputAmountFloat(
        onChange: (String) -> Unit,
        label: String
    ) {
        var text by remember { mutableStateOf(TextFieldValue("")) }

        OutlinedTextField(
            value = text,
            onValueChange = {
                if(it.text.matches(Regex("-?\\d*\\.?\\d*"))) {
                    text = it
                    onChange(it.text)
                }
            },
            label = { Text(text = label) },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(COLOR_PRIMARY),
                focusedIndicatorColor = Color(COLOR_PRIMARY),
                focusedLabelColor = Color(COLOR_PRIMARY),
                unfocusedLabelColor = Color(COLOR_PRIMARY),
                cursorColor = Color(COLOR_PRIMARY)
            )
        )
    }

    @Composable
    fun SendButton(onClick: () -> Unit) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        Button(
            onClick = onClick,
            shape = RectangleShape,
            modifier = Modifier
                .hoverable(interactionSource = interactionSource)
                .height(48.dp)
                .drawBehind {
                    // https://medium.com/@banmarkovic/jetpack-compose-bottom-border-8f1662c2aa84
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .fillMaxWidth(),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if(isHovered) Color(COLOR_BACKGROUND) else Color(COLOR_BUTTON),
                contentColor = Color(COLOR_PRIMARY)
            )
        ) {
            GenericText("SEND DATA")
        }
    }

    @Composable
    fun GenerateButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            GenericText("GENERATE")
        }
    }

    @Composable
    fun CardText(
        text: String,
        weight: Float,
        label: String,
        isEditing: Boolean,
        onTextChange: (String) -> Unit
    ) {
        var text by remember { mutableStateOf(text) }

        if(!isEditing) {
            Text(
                text = text,
                color = Color(COLOR_PRIMARY),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraLight,
                modifier = Modifier
                    .fillMaxWidth(weight)
            )
        }
        else {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    onTextChange(newText)
                },
                label = { Text(text = label) },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color(COLOR_PRIMARY),
                    focusedIndicatorColor = Color(COLOR_PRIMARY),
                    focusedLabelColor = Color(COLOR_PRIMARY),
                    unfocusedLabelColor = Color(COLOR_PRIMARY),
                    cursorColor = Color(COLOR_PRIMARY)
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(10.dp)
                    .widthIn(max = 150.dp)
            )
        }
    }


    @Composable
    fun EditButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color(COLOR_PRIMARY)
            ),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null
            )
        }
    }

    @Composable
    fun DeleteButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                contentColor = Color(COLOR_PRIMARY)
            ),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = null
            )
        }
    }
}