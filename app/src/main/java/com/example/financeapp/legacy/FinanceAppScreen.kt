package com.example.financeapp.legacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.financeapp.R

private const val INITIAL_BALANCE = 25_000
private const val TEST_TRANSACTION_AMOUNT = 100

@Composable
@Suppress("FunctionNaming")
private fun FinanceAppScreen() {
    val balance = INITIAL_BALANCE
    val transactionCount = 0
    var state by remember { mutableStateOf(MainUiState()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 24.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Text(
                text = stringResource(
                    R.string.balance_summary,
                    state.balance,
                    state.counter,
                ),
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(onClick = {
                val transaction = Transaction(
                    TransactionType.DEPOSIT,
                    TEST_TRANSACTION_AMOUNT,
                )
                state = reduce(state, MainUiEvent.AddTransaction(transaction))
            }) {
                Text(text = stringResource(R.string.add_income_button))
            }
            Button(onClick = {
                val transaction = Transaction(
                    TransactionType.WITHDRAW,
                    TEST_TRANSACTION_AMOUNT,
                )
                state = reduce(state, MainUiEvent.AddTransaction(transaction))
            }) {
                Text(text = stringResource(R.string.add_expense_button))
            }
        }
    }
}
