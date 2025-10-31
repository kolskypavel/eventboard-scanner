package cz.eventboard.eventboard_scanner.ui

import androidx.compose.runtime.Composable

/**
 * Platform-specific composable that shows camera preview and calls `onBarcodeDetected` when
 * a barcode/QR code is detected. Implementations should request and manage camera lifecycle.
 *
 * onPreviewReady: optional callback to notify caller whether the native preview has been
 * successfully started (true) or failed (false). Useful for showing debug/placeholder UI.
 */
@Composable
expect fun CameraScanner(
    onBarcodeDetected: (String) -> Unit,
    onToggleFlash: () -> Unit = {},
    isFlashOn: Boolean = false,
    onPreviewReady: (Boolean) -> Unit = {}
)
