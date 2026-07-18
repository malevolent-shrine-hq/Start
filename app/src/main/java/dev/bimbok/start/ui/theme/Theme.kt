package dev.bimbok.start.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.bimbok.start.data.local.models.AppTheme

private val GruvColorScheme = darkColorScheme(
    primary = GruvAqua,
    secondary = GruvYellow,
    tertiary = GruvOrange,
    background = GruvBackground,
    surface = GruvSurface,
    onPrimary = GruvBackground,
    onSecondary = GruvBackground,
    onBackground = GruvIvory,
    onSurface = GruvIvory,
    outline = GruvIvory.copy(alpha = 0.2f)
)

private val CatppuccinColorScheme = darkColorScheme(
    primary = CatTeal,
    secondary = CatYellow,
    tertiary = CatPeach,
    background = CatBackground,
    surface = CatSurface,
    onPrimary = CatBackground,
    onSecondary = CatBackground,
    onBackground = CatText,
    onSurface = CatText,
    outline = CatText.copy(alpha = 0.2f)
)

private val RosePineColorScheme = darkColorScheme(
    primary = RosePine,
    secondary = RoseGold,
    tertiary = RoseLove,
    background = RoseBackground,
    surface = RoseSurface,
    onPrimary = RoseBackground,
    onSecondary = RoseBackground,
    onBackground = RoseText,
    onSurface = RoseText,
    outline = RoseText.copy(alpha = 0.2f)
)

// Theme Helper to get dynamic gradients based on current theme
@Composable
fun getDynamicGradient(): List<Color> {
    val colorScheme = MaterialTheme.colorScheme
    return listOf(colorScheme.primary, colorScheme.tertiary)
}

@Composable
fun getDynamicSecondaryGradient(): List<Color> {
    val colorScheme = MaterialTheme.colorScheme
    return listOf(colorScheme.primary, colorScheme.secondary)
}

@Composable
fun StartTheme(
    theme: AppTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.GRUVBOX -> GruvColorScheme
        AppTheme.CATPPUCCIN -> CatppuccinColorScheme
        AppTheme.ROSE_PINE -> RosePineColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
