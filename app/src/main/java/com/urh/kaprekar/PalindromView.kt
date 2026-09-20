package com.urh.kaprekar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.urh.kaprekar.ui.theme.KapreKarAppTheme
import com.urh.kaprekar.ui.theme.KapreKarTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PalindromView(
    modifier: Modifier = Modifier,
    viewModel: PalindromViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PalindromContent(
        result = state.calculationResult,
        onCalculateClick = viewModel::getPalindromSequence,
        modifier = modifier
    )
}

@Composable
private fun PalindromContent(
    result: String,
    onCalculateClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInputDialog by rememberSaveable { mutableStateOf(false) }

    if (showInputDialog) {
        PalindromInputDialog(
            onDismiss = { showInputDialog = false },
            onConfirm = { number ->
                onCalculateClick(number)
                showInputDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.palindron_title),
            color = KapreKarTheme.colorScheme.primary,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.palindron_explanation),
            color = KapreKarTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (result.isNotEmpty()) {
            Text(
                text = result,
                color = KapreKarTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { showInputDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = KapreKarTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.landing_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PalindromInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var input by rememberSaveable { mutableStateOf("") }
    val number = input.toLongOrNull()
    val isValid = number != null && number in 1L..1_000_000L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.collatz_dialog_title))
        },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { value ->
                    if (value.all(Char::isDigit) && value.length <= 7) {
                        input = value
                    }
                },
                label = {
                    Text(text = stringResource(R.string.collatz_dialog_label))
                },
                supportingText = {
                    Text(text = stringResource(R.string.collatz_dialog_range))
                },
                isError = input.isNotEmpty() && !isValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { number?.let(onConfirm) },
                enabled = isValid
            ) {
                Text(text = stringResource(R.string.collatz_dialog_calculate))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.collatz_dialog_cancel))
            }
        }
    )
}

@PreviewLightDark
@Composable
fun PalindromViewPreview() {
    KapreKarAppTheme {
        PalindromContent(
            result = "121 is a palindrome",
            onCalculateClick = {}
        )
    }
}
