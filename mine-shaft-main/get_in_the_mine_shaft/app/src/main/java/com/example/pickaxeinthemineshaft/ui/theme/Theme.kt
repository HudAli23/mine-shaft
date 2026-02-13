package com.example.pickaxeinthemineshaft.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColors(
    primary = md_theme_light_primary,
    primaryVariant = md_theme_light_primaryContainer,
    secondary = md_theme_light_secondary,
    secondaryVariant = md_theme_light_secondaryContainer,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    error = md_theme_light_error,
    onPrimary = md_theme_light_onPrimary,
    onSecondary = md_theme_light_onSecondary,
    onBackground = md_theme_light_onBackground,
    onSurface = md_theme_light_onSurface,
    onError = md_theme_light_onError
)

private val DarkColors = darkColors(
    primary = md_theme_dark_primary,
    primaryVariant = md_theme_dark_primaryContainer,
    secondary = md_theme_dark_secondary,
    secondaryVariant = md_theme_dark_secondaryContainer,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    error = md_theme_dark_error,
    onPrimary = md_theme_dark_onPrimary,
    onSecondary = md_theme_dark_onSecondary,
    onBackground = md_theme_dark_onBackground,
    onSurface = md_theme_dark_onSurface,
    onError = md_theme_dark_onError
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.primary.toArgb()
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}