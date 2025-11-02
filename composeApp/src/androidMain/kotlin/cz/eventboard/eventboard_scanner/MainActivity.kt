package cz.eventboard.eventboard_scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cz.eventboard.eventboard_scanner.db.AndroidDatabaseDriverFactory
import cz.eventboard.eventboard_scanner.db.DatabaseFactoryProvider
import cz.eventboard.eventboard_scanner.navigation.BackDispatcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the platform-specific database driver factory for common code
        DatabaseFactoryProvider.factory = AndroidDatabaseDriverFactory(this)

        // Use OnBackPressedDispatcher to delegate back events to the shared BackDispatcher.
        onBackPressedDispatcher.addCallback(this) {
            val handled = BackDispatcher.handleBack()
            if (!handled) {
                // If no in-app handler consumed the event, finish the activity (default behavior).
                finish()
            }
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}