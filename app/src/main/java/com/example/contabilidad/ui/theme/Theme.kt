package com.example.contabilidad.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Define el esquema de colores para el tema oscuro de la aplicación.
 * Utiliza los colores institucionales del IT Chetumal.
 */
private val DarkColorScheme = darkColorScheme(
    primary = ITChetumalBlue,
    secondary = ITChetumalOrange,
    tertiary = Pink80
)

/**
 * Define el esquema de colores para el tema claro de la aplicación.
 * Utiliza los colores institucionales del IT Chetumal.
 */
private val LightColorScheme = lightColorScheme(
    primary = ITChetumalBlue,
    secondary = ITChetumalOrange,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Composable principal del tema de la aplicación.
 * Aplica los esquemas de color definidos y configura la barra de estado.
 *
 * @param darkTheme Indica si se debe usar el tema oscuro (basado en la configuración del sistema).
 * @param dynamicColor Desactivado (`false`) para asegurar que siempre se usen nuestros colores personalizados del IT Chetumal.
 * @param content El contenido de la aplicación al que se le aplicará el tema.
 */
@Composable
fun ContabilidadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponible en Android 12+. Lo desactivamos para mantener la marca.
    dynamicColor: Boolean = false,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
