package com.example.financeapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
@Suppress("FunctionNaming", "LongMethod")
internal fun AsyncTransactionsScreen(viewModel: AsyncTransactionsViewModel, onOpenLegacy: () -> Unit) {
    val state = viewModel.uiState

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                stringResource(R.string.async_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            TextButton(onClick = onOpenLegacy) {
                Text(stringResource(R.string.async_open_legacy))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.load(shouldFail = false) }) {
                    Text(stringResource(R.string.async_load))
                }
                Button(onClick = { viewModel.load(shouldFail = true) }) {
                    Text(stringResource(R.string.async_fail))
                }
            }

            if (state.isLoading) {
                Text(stringResource(R.string.async_loading))
            }

            if (state.isError) {
                Text(
                    stringResource(R.string.async_error),
                    color = MaterialTheme.colorScheme.error,
                )
                Button(onClick = { viewModel.load(shouldFail = false) }) {
                    Text(stringResource(R.string.async_retry))
                }
            }

            state.operations.forEach { operation ->
                Text(
                    stringResource(
                        R.string.async_operation,
                        operation.description,
                        operation.amount,
                    ),
                )
            }
        }
    }
}
