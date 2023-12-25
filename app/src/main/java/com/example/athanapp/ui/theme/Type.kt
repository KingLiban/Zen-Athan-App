package com.example.athanapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.athanapp.R
import com.example.compose.md_theme_dark_onTertiary

// Set of Material typography styles to start with
val Sathu = FontFamily(
    Font(R.font.sathu)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Sathu,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        color = md_theme_dark_onTertiary
    ),
    displayMedium = TextStyle(
        fontFamily = Sathu,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = md_theme_dark_onTertiary
    ),
    labelSmall = TextStyle(
        fontFamily = Sathu,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sathu,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)