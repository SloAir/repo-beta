package view.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import view.COLOR_PRIMARY

object Components {
    @Composable
    fun InputAmount(onChange: (String) -> Unit) {
        var text by remember { mutableStateOf(TextFieldValue("0")) }

        OutlinedTextField(
            value = text,
            onValueChange = {
                if(it.text.matches(Regex("\\d+"))) {
                    text = it
                    onChange(it.text)
                }
                if(it.text.matches(Regex(""))) {
                    text = TextFieldValue("0")
                    onChange("0")
                }
            },
            label = { Text(text = "Amount") },
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
    fun GenerateButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = "GENERATE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraLight,
                fontSize = 16.sp,
                color = Color(COLOR_PRIMARY)
            )
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