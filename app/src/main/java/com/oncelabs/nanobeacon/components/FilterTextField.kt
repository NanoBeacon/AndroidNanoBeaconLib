package com.oncelabs.nanobeacon.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oncelabs.nanobeacon.ui.theme.logModalItemBackgroundColor
import com.oncelabs.nanobeacon.ui.theme.placeholderFont

@Composable
fun FilterTextField(
    modifier: Modifier = Modifier,
    state: MutableState<String>,
    placeholder: String,
    trailingIcon: ImageVector = Icons.Default.Close,
    onValueChange: (String) -> Unit = {},
    onlyConfigActive : Boolean
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
            onValueChange(value)
        },
        placeholder = {
            Text(
                style = placeholderFont,
                text = placeholder
            )
        },
        modifier = modifier,
        textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }),
        trailingIcon = {
            if (state.value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        state.value = ""
                        onValueChange("")
                    }
                ) {
                    Icon(
                        trailingIcon,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor =  Color.White,
            unfocusedIndicatorColor = Color.White,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FilterTextFieldPreviews() {
    val textState = remember { mutableStateOf("") }
    FilterTextField(
        state = textState,
        placeholder = "placeholder",
        onlyConfigActive = false
    )
}