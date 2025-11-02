package cz.eventboard.eventboard_scanner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.eventboard.eventboard_scanner.AppViewModel
import eventboard_scanner.composeapp.generated.resources.Res
import eventboard_scanner.composeapp.generated.resources.code_label
import eventboard_scanner.composeapp.generated.resources.enter_button
import eventboard_scanner.composeapp.generated.resources.error_not_valid
import eventboard_scanner.composeapp.generated.resources.eventboard
import eventboard_scanner.composeapp.generated.resources.logo_content_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * Isolated login view. It validates a 6-digit code using the provided AppViewModel
 * and calls `onLoginSuccess()` when the check passes.
 */
@Composable
fun LoginView(
    viewModel: AppViewModel = AppViewModel(),
    onLoginSuccess: () -> Unit = {}
) {
    val logoDesc = stringResource(Res.string.logo_content_description)
    val codeLabel = stringResource(Res.string.code_label)
    val enterButton = stringResource(Res.string.enter_button)
    val errorNotSixDigits = stringResource(Res.string.error_not_valid)

    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.eventboard),
            contentDescription = logoDesc,
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = code,
            onValueChange = { input ->
                val filtered = input.filter { it.isDigit() }
                val limited = if (filtered.length <= 6) filtered else filtered.take(6)
                code = limited
                if (limited.length == 6) error = null
            },
            label = { Text(codeLabel) },
            singleLine = true,
            modifier = Modifier.width(200.dp),
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            if (code.length == 6) {
                if (viewModel.getEventByCode(code.toInt())) {
                    error = null
                    onLoginSuccess()
                } else {
                    error = errorNotSixDigits
                }
            } else {
                error = errorNotSixDigits
            }
        }) {
            Text(enterButton)
        }
    }
}
