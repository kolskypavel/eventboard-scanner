package cz.eventboard.eventboard_scanner

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.eventboard.eventboard_scanner.ui.AppTheme
import cz.eventboard.eventboard_scanner.ui.LoginView
import cz.eventboard.eventboard_scanner.ui.MainView
import cz.eventboard.eventboard_scanner.ui.TicketCheckView
import cz.eventboard.eventboard_scanner.ui.TicketsListView
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.eventboard
import eventboard_scanner.composeapp.generated.resources.location_label
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

// Top-level navigation type so it's usable from composables (sealed classes cannot be local).
sealed class Screen {
    object Login : Screen()
    object Main : Screen()
    object Tickets : Screen()
    data class CheckParticipant(val initialTicketId: String? = null) : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    val expectedCode = "123456"

    // Simple in-memory navigation
    var navStack by remember { mutableStateOf<List<Screen>>(listOf(Screen.Login)) }
    fun currentScreen() = navStack.last()

    // Allow normal navigation between screens. The Login button enforces that Main is only reached
    // after a correct code, so no additional guards are required here.
    fun navigate(to: Screen) {
        navStack = navStack + to
    }

    fun goBack() {
        if (navStack.size > 1) navStack = navStack.dropLast(1)
    }

    // Use AppTheme (Material3) so primary/secondary colors from Theme.kt are applied across the app.
    AppTheme(darkTheme = isSystemInDarkTheme()) {
        // sample tickets
        var tickets by remember {
            mutableStateOf(
                listOf(
                    cz.eventboard.eventboard_scanner.ui.TicketItem("A-001", "Alice Novak", checkedIn = true),
                    cz.eventboard.eventboard_scanner.ui.TicketItem("A-002", "Bob Černý", checkedIn = false),
                    cz.eventboard.eventboard_scanner.ui.TicketItem("B-101", "Carmen Ruiz", checkedIn = false),
                )
            )
        }

        // register back handler for fragment-like behavior
        DisposableEffect(navStack) {
            val handler = {
                if (navStack.size > 1) {
                    // consume back and pop the in-app stack
                    true.also { goBack() }
                } else {
                    // allow platform to handle (close app)
                    false
                }
            }
            BackDispatcher.register(handler)
            onDispose { BackDispatcher.unregister(handler) }
        }

        when (val screen = currentScreen()) {
            is Screen.Login -> {
                // Use the extracted LoginView
                LoginView(expectedCode) { navigate(Screen.Main) }
            }

            is Screen.Main -> {
                MainView(
                    eventName = stringResource(Res.string.eventboard),
                    location = stringResource(Res.string.location_label),
                    insideCount = tickets.count { it.checkedIn },
                    capacity = 1000,
                    onParticipants = { navigate(Screen.Tickets) },
                    // changed: Check Tickets should open the ticket check screen directly
                    onCheckTickets = { navigate(Screen.CheckParticipant()) },
                    onBack = { goBack() }
                )
            }

            is Screen.Tickets -> {
                // Tickets screen with back to Main
                Column(modifier = Modifier.fillMaxSize()) {
                    // simple top-row back control
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        IconButton(onClick = { goBack() }) {
                            Text(text = "<")
                        }
                    }
                    TicketsListView(tickets = tickets) { ticketId ->
                        // when checking a ticket from the list, navigate to check participant with prefilled id
                        navigate(Screen.CheckParticipant(initialTicketId = ticketId))
                    }
                }
            }

            is Screen.CheckParticipant -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        IconButton(onClick = { goBack() }) {
                            Text(text = "<")
                        }
                    }
                    TicketCheckView(
                        initialManualInput = screen.initialTicketId ?: "",
                        onScan = { scanned ->
                            // handle scan result: for demo we mark ticket as checked in
                            tickets = tickets.map { if (it.id == scanned) it.copy(checkedIn = true) else it }
                            goBack()
                        },
                        onManualCheck = { id ->
                            tickets = tickets.map { if (it.id == id) it.copy(checkedIn = true) else it }
                            goBack()
                        },
                        onToggleFlash = {},
                        isFlashOn = false,
                        onBack = { goBack() }
                    )
                }
            }
        }
    }
}