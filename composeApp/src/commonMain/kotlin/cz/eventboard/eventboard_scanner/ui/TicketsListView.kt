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

import org.jetbrains.compose.resources.stringResource
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.search_tickets_label
import eventboard_scanner.composeapp.generated.resources.no_tickets_found
import eventboard_scanner.composeapp.generated.resources.check_button
import eventboard_scanner.composeapp.generated.resources.status_inside
import eventboard_scanner.composeapp.generated.resources.status_outside
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tickets list view with a search bar on top (no top-level "Check Tickets" button).
 * - tickets: list of TicketItem
 * - onCheckTicket: callback for checking a single ticket
 */

data class TicketItem(val id: String, val holderName: String, val checkedIn: Boolean = false)

@Composable
fun TicketsListView(
    tickets: List<TicketItem>,
    onCheckTicket: (String) -> Unit = {}
) {
    val queryState = remember { mutableStateOf("") }
    val qLower = queryState.value.trim().lowercase()
    val filtered = tickets.filter { t ->
        qLower.isEmpty() || t.id.lowercase().contains(qLower) || t.holderName.lowercase().contains(qLower)
    }

    val searchLabel = stringResource(Res.string.search_tickets_label)
    val noTicketsText = stringResource(Res.string.no_tickets_found)
    val checkText = stringResource(Res.string.check_button)
    val statusInsideText = stringResource(Res.string.status_inside)
    val statusOutsideText = stringResource(Res.string.status_outside)

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

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = noTicketsText, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { ticket ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                        ) {
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = ticket.holderName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "ID: ${ticket.id}", style = MaterialTheme.typography.bodyMedium)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val statusText = if (ticket.checkedIn) statusInsideText else statusOutsideText
                                    Text(text = statusText, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = { onCheckTicket(ticket.id) }) {
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
    val sample = listOf(
        TicketItem(id = "A-001", holderName = "Alice Novak", checkedIn = true),
        TicketItem(id = "A-002", holderName = "Bob Černý", checkedIn = false),
        TicketItem(id = "B-101", holderName = "Carmen Ruiz", checkedIn = false),
    )
    TicketsListView(tickets = sample, onCheckTicket = {})
}
