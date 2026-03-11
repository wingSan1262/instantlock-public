package com.risyan.quickshutdownphone.base.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val BaseTextStyle by lazy {
    TextStyle(
        letterSpacing = 0.15.sp,
    )
}
val HeaderStyle by lazy {
    BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = OFF_BLACK_414249,
        lineHeight = 32.sp,
    )
}

val Header2Style by lazy {
    HeaderStyle.copy(
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif
    )
}

val Header3Style by lazy {
    HeaderStyle.copy(
        fontSize = 16.sp,
    )
}

val BodyStyle by lazy {
    BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = GRAY_6e7582,
        lineHeight = 28.sp,
    )
}

val Body2Style by lazy {
    BodyStyle.copy(
        fontSize = 14.sp,
    )
}

val Body3Style by lazy {
    BodyStyle.copy(
        fontSize = 12.sp,
    )
}

val DateStyle by lazy {
    BaseTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = GRAY_a5adb9
    )
}