package com.oncelabs.nanobeacon.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.oncelabs.nanobeacon.R


val helveticaNeue = FontFamily(
    Font(R.font.helvetica_neue)
)

val logTextFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = logTextColor
)

val logTitleFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 25.sp,
    color = logTextColor
)

val logModalTitleFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 25.sp,
    color = logTextColor
)

val logModalItemFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    color = logTextColor
)

val logButtonFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Light,
    fontSize = 20.sp,
    color = logAddButtonColor
)

val logDoneFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    color = logTextColor
)



val liveCardNameFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    color = Color.White
)

val liveTypeFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    color = chartColor
)

val liveSubTypeFont = TextStyle(
    fontFamily = helveticaNeue,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    color = chartColor
)


// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)