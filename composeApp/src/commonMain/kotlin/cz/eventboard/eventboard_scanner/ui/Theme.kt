package cz.eventboard.eventboard_scanner.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define app-wide colors and color schemes (light + dark).
// These are intentionally simple and can be extended later.

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF5170FF), // updated to match drawable eventboard.xml (#5170ff)
    onPrimary = Color.White,
    secondary = Color(0xFF006D3F),
    onSecondary = Color.White,
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF101213),
    surface = Color.White,
    onSurface = Color(0xFF101213),
    error = Color(0xFFB00020),
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF002F5A),
    secondary = Color(0xFF80D291),
    onSecondary = Color(0xFF00220D),
    background = Color(0xFF0F1112),
    onBackground = Color(0xFFE6EDF0),
    surface = Color(0xFF121314),
    onSurface = Color(0xFFE6EDF0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

// Status colors as extension properties on ColorScheme so they can be accessed via
// MaterialTheme.colorScheme.success / .invalid throughout the app.
val ColorScheme.success: Color
    get() = Color(0xFF2E7D32)

val ColorScheme.invalid: Color
    get() = Color(0xFFC62828)

/**
 * AppTheme wrapper that selects a color scheme and applies Material3 theming.
 * - darkTheme: whether to use the dark color scheme
 */
@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
