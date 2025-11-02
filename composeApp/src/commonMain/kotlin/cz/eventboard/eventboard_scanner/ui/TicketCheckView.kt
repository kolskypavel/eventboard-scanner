// kotlin
package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.eventboard.eventboard_scanner.AppViewModel
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.check_button
import eventboard_scanner.composeapp.generated.resources.manual_check_title
import eventboard_scanner.composeapp.generated.resources.manual_ticket_hint
import org.jetbrains.compose.resources.stringResource

@Suppress("UNUSED_PARAMETER")
@Composable
fun TicketCheckView(
    viewModel: AppViewModel = AppViewModel(),
    initialManualInput: String = "",
    isFlashOn: Boolean,
    onBack: () -> Unit = {},
    onShowStatusScreen: (status: TicketStatus, code: String?) -> Unit = { _, _ -> },
    onToggleFlash: () -> Unit = {}
) {
    val manualTitle = stringResource(Res.string.manual_check_title)
    val manualHint = stringResource(Res.string.manual_ticket_hint)
    val checkLabel = stringResource(Res.string.check_button)

    var manualCode by remember { mutableStateOf(initialManualInput) }
    var lastScanned by remember { mutableStateOf<String?>(null) }
    var flashOn by remember { mutableStateOf(isFlashOn) }

    // Lock scanning while a ticket is being handled (prevents multiple detections of same code)
    var scanLocked by remember { mutableStateOf(false) }

    // Re-enable scanning when the status screen clears the lastCheckedStatus in the shared ViewModel.
    // NavGraph will call viewModel.clearLastChecked() when returning from the status screen.
    LaunchedEffect(viewModel.lastCheckedStatus) {
        if (viewModel.lastCheckedStatus == null) {
            scanLocked = false
        }
    }

    // Top-level Box so we can render an overlay dialog that covers the whole screen
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top: camera preview (takes ~60% of the height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
            ) {
                CameraScanner(
                    onBarcodeDetected = { code ->
                        if (scanLocked) return@CameraScanner
                        // immediately lock to avoid duplicate detections
                        scanLocked = true
                         lastScanned = code

                         // Try to parse scanned string into Long; only check when parsing succeeds
                         val parsed = code.trim().toLongOrNull()
                         if (parsed != null) {
                              val status = viewModel.validateTicket(parsed)
                             viewModel.setLastChecked(status, code)
                             onShowStatusScreen(status, code)
                         }
                     },
                     onToggleFlash = {
                         flashOn = !flashOn
                         onToggleFlash()
                     },
                     isFlashOn = flashOn
                 )
             }

            HorizontalDivider()

            // Bottom: manual input and actions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = manualTitle, style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = manualCode,
                    onValueChange = { manualCode = it },
                    label = { Text(manualHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // Only keep the Check button here; back navigation is handled by the parent/top bar.
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            if (scanLocked) return@Button
                            scanLocked = true
                            val code = manualCode.trim().toLongOrNull()
                            if (code != null) {
                                val status = viewModel.validateTicket(code)
                                viewModel.setLastChecked(status, code.toString())
                                onShowStatusScreen(status, code.toString())
                            }
                        }) {
                            Text(checkLabel)
                        }
                    }
                }

                lastScanned?.let {
                    Text(text = "Last scanned: $it", style = MaterialTheme.typography.bodySmall)
                }

                // Previously the dialog was inside this Column which limited its size. The dialog
                // has been moved to the Box overlay (below) so nothing is rendered here now.
            }
        }
    }
}

@Composable
fun TicketCheckView(
    viewModel: AppViewModel = AppViewModel(),
    initialManualInput: String = "",
    isFlashOn: Boolean,
    onBack: () -> Unit = {},
    onShowStatusScreen: (status: TicketStatus, code: String?) -> Unit = { _, _ -> }
) {
    // Delegate to the full signature with a no-op onToggleFlash to preserve API compatibility.
    TicketCheckView(viewModel, initialManualInput, isFlashOn, onBack, onShowStatusScreen, onToggleFlash = {})
}
