package cz.eventboard.eventboard_scanner.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@Composable
actual fun CameraScanner(
    onBarcodeDetected: (String) -> Unit,
    onToggleFlash: () -> Unit,
    isFlashOn: Boolean,
    onPreviewReady: (Boolean) -> Unit
) {
    val context = LocalContext.current
    // Prefer the hosting Activity as LifecycleOwner (ComponentActivity implements LifecycleOwner)
    val lifecycleOwner = (context as? androidx.activity.ComponentActivity)

    var hasPermission by remember { mutableStateOf(
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    ) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
    }

    // Auto-launch permission prompt once when missing
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    // single-threaded executor for image analysis
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    // make sure we shut it down when composable leaves
    DisposableEffect(Unit) {
        onDispose {
            try { cameraExecutor.shutdown() } catch (_: Exception) {}
        }
    }

    if (!hasPermission) {
        // Show simple request UI when camera permission is missing or pending
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = "Camera permission is required to scan tickets")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                Text(text = "Request camera permission")
            }
        }
        onPreviewReady(false)
        return
    }

    // Ensure we have a lifecycle owner to bind to; if not, report preview unavailable
    if (lifecycleOwner == null) {
        onPreviewReady(false)
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Unable to access hosting Activity for lifecycle")
        }
        return
    }

    // Permission granted and lifecycle owner available: show camera preview
    AndroidView(factory = { ctx ->
        val container = FrameLayout(ctx)
        val previewView = PreviewView(ctx).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        container.addView(previewView)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val barcodeScanner = BarcodeScanning.getClient()

                val analysis = ImageAnalysis.Builder().build()
                analysis.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes: List<Barcode> ->
                                for (barcode in barcodes) {
                                    val raw = barcode.rawValue
                                    if (!raw.isNullOrEmpty()) {
                                        onBarcodeDetected(raw)
                                        break
                                    }
                                }
                            }
                            .addOnFailureListener { e -> Log.e("CameraScanner", "Barcode scan failed", e) }
                            .addOnCompleteListener { imageProxy.close() }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
                onPreviewReady(true)

            } catch (e: Exception) {
                Log.e("CameraScanner", "Failed to bind camera", e)
                onPreviewReady(false)
            }
        }, ContextCompat.getMainExecutor(ctx))

        container
    }, modifier = Modifier.fillMaxSize())
}
