package com.example.financeapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

internal class TransactionViewModel : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    fun onEvent(event: MainUiEvent) {
        uiState = reduce(uiState, event)
    }

    private fun reduce(
        state: MainUiState,
        event: MainUiEvent
    ): MainUiState {
        return when (event) {

            is MainUiEvent.AmountChanged -> {
                if (event.value.all { it.isDigit() }) {
                    state.copy(
                        amount = event.value,
                        error = null
                    )
                } else {
                    state
                }
            }

            is MainUiEvent.DescriptionChanged -> {
                state.copy(
                    description = event.value,
                    error = null
                )
            }

            is MainUiEvent.TypeChanged -> {
                state.copy(
                    type = event.type,
                    error = null
                )
            }

            MainUiEvent.InvalidTransaction -> {
                state.copy(
                    error = TransactionError.INVALID_INPUT
                )
            }

            MainUiEvent.AmountTooLarge -> {
                state.copy(
                    error = TransactionError.AMOUNT_TOO_LARGE
                )
            }

            is MainUiEvent.AddTransaction -> {
                val tx = event.transaction

                if (tx.amount <= 0 || tx.description.isBlank()) {
                    state.copy(
                        error = TransactionError.INVALID_INPUT
                    )
                } else {
                    val change = if (
                        tx.type == TransactionType.INCOME
                    ) {
                        tx.amount.toLong()
                    } else {
                        -tx.amount.toLong()
                    }

                    val next = state.balance + change

                    state.copy(
                        amount = "",
                        description = "",
                        balance = next.toInt(),
                        transactionCount = state.transactionCount + 1,
                        transactions = state.transactions + tx,
                        error = null
                    )
                }
            }
        }
    }

    fun addTransaction() {
        val description = uiState.description.trim()

        if (uiState.amount.isEmpty() || description.isEmpty()) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }

        val amount = uiState.amount.toIntOrNull()

        if (amount == null) {
            onEvent(MainUiEvent.AmountTooLarge)
            return
        }

        if (amount <= 0) {
            onEvent(MainUiEvent.InvalidTransaction)
            return
        }

        onEvent(
            MainUiEvent.AddTransaction(
                Transaction(
                    amount = amount,
                    type = uiState.type,
                    description = description
                )
            )
        )
    }
}