package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed class TicketStatus {
    object Valid : TicketStatus()
    object Invalid : TicketStatus()
    data class Unknown(val reason: String? = null) : TicketStatus()
}

private data class DialogUi(
    val iconText: String,
    val iconTint: Color,
    val title: String,
    val message: String
)

/**
 * Simple dialog composable to show ticket verification result.
 * - status: the ticket status to display
 * - onDismiss: called when the dialog should be closed
 * - onDetails: optional callback when user requests details
 */
@Composable
fun TicketStatusDialog(
    status: TicketStatus,
    onDismiss: () -> Unit,
    onDetails: (() -> Unit)? = null
) {
    // Map status -> UI values using a small data holder.
    val ui = when (status) {
        is TicketStatus.Valid -> DialogUi(
            iconText = "✓",
            iconTint = MaterialTheme.colorScheme.success,
            title = "Ticket valid",
            message = "This ticket is valid. Welcome!"
        )
        is TicketStatus.Invalid -> DialogUi(
            iconText = "✖",
            iconTint = MaterialTheme.colorScheme.invalid,
            title = "Ticket invalid",
            message = "This ticket is not valid. Please check the ticket or contact support."
        )
        is TicketStatus.Unknown -> DialogUi(
            iconText = "?",
            iconTint = MaterialTheme.colorScheme.onSurface,
            title = "Unknown status",
            message = status.reason ?: "The ticket status could not be determined."
        )
    }

    Surface(
        modifier = Modifier.padding(16.dp),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = ui.iconText, color = ui.iconTint, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(text = ui.title, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = ui.message, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (onDetails != null) {
                    Button(onClick = onDetails) {
                        Text(text = "Details")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDismiss) {
                    Text(text = "Close")
                }
            }
        }
    }
}