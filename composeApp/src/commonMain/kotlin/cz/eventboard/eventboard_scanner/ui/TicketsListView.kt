package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import cz.eventboard.eventboard_scanner.AppViewModel
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus

import org.jetbrains.compose.resources.stringResource
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.search_tickets_label
import eventboard_scanner.composeapp.generated.resources.no_tickets_found
import eventboard_scanner.composeapp.generated.resources.check_button
import eventboard_scanner.composeapp.generated.resources.status_cancelled
import eventboard_scanner.composeapp.generated.resources.status_inside
import eventboard_scanner.composeapp.generated.resources.status_invalid
import eventboard_scanner.composeapp.generated.resources.status_outside
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tickets list view with a search bar on top (no top-level "Check Tickets" button).
 * - viewModel: optional AppViewModel to source tickets
 * - tickets: optional override list of TicketItem
 * - onCheckTicket: callback for checking a single ticket
 */

@Composable
fun TicketsListView(
    viewModel: AppViewModel = AppViewModel(),
    onCheckTicket: ((Int) -> Unit)? = null
) {
    val searchLabel = stringResource(Res.string.search_tickets_label)
    val noTicketsText = stringResource(Res.string.no_tickets_found)
    val checkText = stringResource(Res.string.check_button)
    val statusInsideText = stringResource(Res.string.status_inside)
    val statusOutsideText = stringResource(Res.string.status_outside)
    val statusCancelledText = stringResource(Res.string.status_cancelled)
    val statusInvalidText = stringResource(Res.string.status_invalid)

    val tickets = viewModel.tickets
    val queryState = remember { mutableStateOf("") }

    fun getTicketStatus(ticketStatus: TicketStatus): String {
        return when (ticketStatus) {
            TicketStatus.CHECKED -> statusInsideText
            TicketStatus.VALID -> statusOutsideText
            TicketStatus.INVALID -> statusInvalidText
            TicketStatus.CANCELLED -> statusCancelledText
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Search field full width (removed top-level check button)
            OutlinedTextField(
                value = queryState.value,
                onValueChange = { queryState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                label = { Text(searchLabel) }
            )

            if (tickets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = noTicketsText, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tickets, key = { it.id }) { ticket ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ticket.owner,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "ID: ${ticket.id}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val statusText = getTicketStatus(ticket.status)
                                    Text(text = statusText, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = {
                                        viewModel.checkInTicket(ticket.id)
                                    }) {
                                        Text(text = checkText)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TicketsListViewPreview() {
    val vm = AppViewModel().apply { populateSampleData() }
    TicketsListView(viewModel = vm, onCheckTicket = {})
}
