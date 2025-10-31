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
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.check_button
import eventboard_scanner.composeapp.generated.resources.manual_check_title
import eventboard_scanner.composeapp.generated.resources.manual_ticket_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun TicketCheckView(
    initialManualInput: String = "",
    onScan: (String) -> Unit,
    onManualCheck: (String) -> Unit,
    onToggleFlash: () -> Unit,
    isFlashOn: Boolean,
    onBack: () -> Unit = {}
) {
    val manualTitle = stringResource(Res.string.manual_check_title)
    val manualHint = stringResource(Res.string.manual_ticket_hint)
    val checkLabel = stringResource(Res.string.check_button)

    var manualCode by remember { mutableStateOf(initialManualInput) }
    var lastScanned by remember { mutableStateOf<String?>(null) }
    var flashOn by remember { mutableStateOf(isFlashOn) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top: camera preview (takes ~60% of the height)
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.6f)
        ) {
            CameraScanner(
                onBarcodeDetected = { code ->
                    lastScanned = code
                    onScan(code)
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
                        val code = manualCode.trim()
                        if (code.isNotEmpty()) {
                            onManualCheck(code)
                        }
                    }) {
                        Text(checkLabel)
                    }
                }
            }

            lastScanned?.let {
                Text(text = "Last scanned: $it", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
