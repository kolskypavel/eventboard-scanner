package cz.eventboard.eventboard_scanner.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.eventboard.eventboard_scanner.AppViewModel
import cz.eventboard.eventboard_scanner.db.entity.TicketStatus
import cz.eventboard.eventboard_scanner.ui.LoginView
import cz.eventboard.eventboard_scanner.ui.MainView
import cz.eventboard.eventboard_scanner.ui.TicketCheckView
import cz.eventboard.eventboard_scanner.ui.TicketsListView
import cz.eventboard.eventboard_scanner.ui.TicketStatusScreen

/**
 * Navigation routes used in the app.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Main : Screen("main")
    object Tickets : Screen("tickets")
    object CheckParticipant : Screen("checkParticipant") {
        fun createRoute() = "checkParticipant"
    }
    object TicketStatus : Screen("ticketStatus") {
        fun createRoute() = "ticketStatus"
    }
}

@Composable
fun AppNavGraph(viewModel: AppViewModel = AppViewModel()) {
    val navController = rememberNavController()

    // Register a back handler to pop the navController if possible
    DisposableEffect(navController) {
        val handler = {
            if (navController.previousBackStackEntry != null) {
                navController.popBackStack()
                true
            } else {
                false
            }
        }
        BackDispatcher.register(handler)
        onDispose { BackDispatcher.unregister(handler) }
    }

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginView(viewModel) {
                navController.navigate(Screen.Main.route)
            }
        }

        composable(Screen.Main.route) {
            // MainView uses the shared viewModel for state
            MainView(
                viewModel = viewModel,
                onParticipants = { navController.navigate(Screen.Tickets.route) },
                onCheckTickets = { navController.navigate(Screen.CheckParticipant.createRoute()) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Tickets.route) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    IconButton(onClick = { navController.popBackStack() }) { Text(text = "<") }
                }
                // TicketsListView now reads tickets from the shared viewModel.
                TicketsListView(viewModel = viewModel) {
                    navController.navigate(Screen.CheckParticipant.createRoute())
                }
            }
        }

        composable(Screen.CheckParticipant.route) {
            TicketCheckView(
                viewModel,
                "",
                false,
                { navController.popBackStack() },
                { _, _ ->
                    // navigation only; business logic (setLastChecked) is done in the view
                    navController.navigate(Screen.TicketStatus.createRoute())
                }
            )
        }

        composable(Screen.TicketStatus.route) {
            // Read status and code from the shared viewModel
            val status = viewModel.lastCheckedStatus ?: TicketStatus.INVALID
            val code = viewModel.lastCheckedCode
            TicketStatusScreen(
                viewModel,
                status,
                code,
                {
                    // clear last-checked so the scanner in TicketCheckView can resume
                    viewModel.clearLastChecked()
                    navController.popBackStack()
                }
             )
         }
    }
}
