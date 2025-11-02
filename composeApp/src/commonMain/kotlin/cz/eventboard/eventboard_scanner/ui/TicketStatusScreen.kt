package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.eventboard.eventboard_scanner.AppViewModel
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus
import cz.eventboard.eventboard_scanner.navigation.BackDispatcher
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.ticket_status_cancelled
import eventboard_scanner.composeapp.generated.resources.ticket_status_checked
import eventboard_scanner.composeapp.generated.resources.ticket_status_code
import eventboard_scanner.composeapp.generated.resources.ticket_status_confirm
import eventboard_scanner.composeapp.generated.resources.ticket_status_continue
import eventboard_scanner.composeapp.generated.resources.ticket_status_date
import eventboard_scanner.composeapp.generated.resources.ticket_status_invalid
import eventboard_scanner.composeapp.generated.resources.ticket_status_title
import eventboard_scanner.composeapp.generated.resources.ticket_status_valid
import org.jetbrains.compose.resources.stringResource

@Composable
fun TicketStatusScreen(
    viewModel: AppViewModel,
    status: TicketStatus,
    ticketCode: String?,
    onClose: () -> Unit,
) {
    // Register a back handler that clears lastChecked and delegates to onClose
    DisposableEffect(Unit) {
        val handler = {
            viewModel.clearLastChecked()
            onClose()
            true
        }
        BackDispatcher.register(handler)
        onDispose { BackDispatcher.unregister(handler) }
    }

    // Simple UI mapping
    val bgColor: Color = when (status) {
        TicketStatus.VALID -> MaterialTheme.colorScheme.primary
        TicketStatus.CHECKED -> MaterialTheme.colorScheme.error
        TicketStatus.INVALID -> MaterialTheme.colorScheme.error
        TicketStatus.CANCELLED -> MaterialTheme.colorScheme.surface
    }

    val title: String = when (status) {
        TicketStatus.VALID -> stringResource( Res.string.ticket_status_valid)
        TicketStatus.CHECKED -> stringResource(Res.string.ticket_status_checked)
        TicketStatus.INVALID -> stringResource(Res.string.ticket_status_invalid)
        TicketStatus.CANCELLED -> stringResource( Res.string.ticket_status_cancelled)
    }

    val iconText: String = when (status) {
        TicketStatus.VALID -> "👍"
        TicketStatus.CHECKED -> "⚠️"
        TicketStatus.INVALID -> "✖"
        TicketStatus.CANCELLED -> "!"
    }

    val detailsTitle = stringResource(Res.string.ticket_status_title)
    val codeLabel = stringResource( Res.string.ticket_status_code)
    val dateLabel = stringResource(Res.string.ticket_status_date)
    val confirmLabel = stringResource(Res.string.ticket_status_confirm)
    val continueLabel = stringResource(Res.string.ticket_status_continue)

    Column(modifier = Modifier.fillMaxSize().background(bgColor)) {
        // Top content - takes available space, centers title+icon
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = iconText, fontSize = 88.sp, textAlign = TextAlign.Center)
        }

        // Bottom card with ticket details and action (sibling, not overlay)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Confirm pill at top (only for valid tickets)
                if (status == TicketStatus.VALID) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = {
                                // perform confirm business logic here in the UI layer
                                val id = ticketCode?.toLongOrNull()
                                if (id != null) {
                                    // checkInTicket mutates state and returns true on success
                                    val ok = viewModel.checkInTicket(id)
                                    val newStatus = if (ok) TicketStatus.CHECKED else viewModel.validateTicket(id)
                                    viewModel.setLastChecked(newStatus, ticketCode)
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(text = confirmLabel)
                        }
                    }
                }

                Text(text = detailsTitle, style = MaterialTheme.typography.titleMedium)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = codeLabel)
                    Text(text = ticketCode ?: "-")
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = dateLabel)
                    Text(text = "-")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = {
                        viewModel.clearLastChecked()
                        onClose()
                    }) {
                        Text(text = continueLabel)
                    }
                }
            }
        }
    }
}
