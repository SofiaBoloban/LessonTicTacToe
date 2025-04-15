package com.example.lessontictactoe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SoftBlue80,
    onPrimary = Color.White,
    primaryContainer = SoftBlue40,
    onPrimaryContainer = Color.White,
    secondary = NeutralGrey80,
    onSecondary = Color.White,
    secondaryContainer = NeutralGrey40,
    onSecondaryContainer = Color.White,
    tertiary = Pink80,
    onTertiary = Color.White,
    tertiaryContainer = Pink40,
    onTertiaryContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = SoftBlue,
    onPrimary = Color.White,
    primaryContainer = SoftBlue80,
    onPrimaryContainer = Color.Black,
    secondary = NeutralGrey,
    onSecondary = Color.White,
    secondaryContainer = NeutralGrey80,
    onSecondaryContainer = Color.Black,
    tertiary = Pink40,
    onTertiary = Color.White,
    tertiaryContainer = Pink80,
    onTertiaryContainer = Color.Black,
    background = Color(0xFFFFFBFE),
    onBackground = Color.Black,
    surface = Color(0xFFFFFBFE),
    onSurface = Color.Black,
)

@Composable
fun LessonTicTacToeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}