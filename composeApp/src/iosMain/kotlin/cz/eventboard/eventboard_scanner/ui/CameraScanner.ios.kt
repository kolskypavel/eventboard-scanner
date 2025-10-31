package cz.eventboard.eventboard_scanner.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * iOS placeholder implementation of CameraScanner: shows a simple UI and allows
 * simulating a scan result. This keeps multiplatform builds green where native
 * camera implementation is not provided.
 */
@Composable
actual fun CameraScanner(
    onBarcodeDetected: (String) -> Unit,
    onToggleFlash: () -> Unit,
    isFlashOn: Boolean,
    onPreviewReady: (Boolean) -> Unit
) {
    // No native camera preview in this placeholder – report preview not available
    onPreviewReady(false)

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = "Camera preview not available")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onBarcodeDetected("SIMULATED-IOS-QR-123") }) {
            Text(text = "Simulate scan")
        }
    }
}
