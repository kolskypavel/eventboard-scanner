package cz.eventboard.eventboard_scanner

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import cz.eventboard.eventboard_scanner.ui.AppTheme
import cz.eventboard.eventboard_scanner.navigation.AppNavGraph
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    AppTheme(darkTheme = isSystemInDarkTheme()) {
        val appViewModel = AppViewModel().apply { populateSampleData() }
        AppNavGraph(viewModel = appViewModel)
    }
}