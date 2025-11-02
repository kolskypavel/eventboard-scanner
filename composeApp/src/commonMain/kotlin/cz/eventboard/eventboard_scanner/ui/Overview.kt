package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import cz.eventboard.eventboard_scanner.AppViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

import org.jetbrains.compose.resources.stringResource
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.detailed_overview
import eventboard_scanner.composeapp.generated.resources.eventboard
import eventboard_scanner.composeapp.generated.resources.inside_label
import eventboard_scanner.composeapp.generated.resources.participants_button
import eventboard_scanner.composeapp.generated.resources.location_label
import eventboard_scanner.composeapp.generated.resources.check_tickets_button

/**
 * Main view shown after successful login / code entry.
 * Shows a basic overview of the event, a detailed section with how many
 * people are currently inside and a button to navigate to the participants screen.
 *
 * Inputs:
 * - viewModel: supplies insideCount and capacity
 * - onParticipants: callback invoked when "Participants" button is pressed
 * - onCheckTickets: callback for the check tickets button
 * - onBack: optional back navigation callback (shown as a small back control)
 */
@Composable
fun MainView(
    viewModel: AppViewModel = AppViewModel(),
    onParticipants: () -> Unit = {},
    onCheckTickets: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    // Titles are now defined by the screen itself via string resources
    val eventName = stringResource(Res.string.eventboard)
    val location = stringResource(Res.string.location_label)
    val detailedOverviewText = stringResource(Res.string.detailed_overview)
    val insideLabelText = stringResource(Res.string.inside_label)
    val participantsText = stringResource(Res.string.participants_button)
    val checkTicketsText = stringResource(Res.string.check_tickets_button)

    val insideCount = viewModel.checkedCount
    val capacity = viewModel.event

    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Small inline back control
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Text(text = "<")
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Basic overview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = eventName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    if (location.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = location, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detailed overview card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = detailedOverviewText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))

                    // People count
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = insideLabelText, style = MaterialTheme.typography.bodyLarge)
                        val capacityText = capacity?.let { " / $it" } ?: ""
                        Text(text = "$insideCount$capacityText", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onParticipants, modifier = Modifier.fillMaxWidth()) {
                        Text(text = participantsText)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Check Tickets button under Participants
                    Button(onClick = onCheckTickets, modifier = Modifier.fillMaxWidth()) {
                        Text(text = checkTicketsText)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainViewPreview() {
    val vm = AppViewModel().apply { populateSampleData() }
    MainView(
        viewModel = vm,
        onParticipants = {},
        onCheckTickets = {},
        onBack = {}
    )
}
