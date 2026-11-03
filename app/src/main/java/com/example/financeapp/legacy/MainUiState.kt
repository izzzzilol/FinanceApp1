package com.example.financeapp.legacy

internal data class MainUiState(
    // Какое начальное значение у баланса?
    // Как посчитать выполненные операции?
    val balance: Int = 0,
    val counter: Int = 0,
)

internal enum class TransactionType {
    // Какие два типа нужны кнопкам?
    DEPOSIT,
    WITHDRAW,
}

internal data class Transaction(
    // Какие два свойства полностью описывают операцию?
    val transactionType: TransactionType,
    val amount: Int,
)

internal sealed interface MainUiEvent {
    // Одно событие должно переносить созданную транзакцию
    data class AddTransaction(
        val transaction: Transaction,
    ) : MainUiEvent
}

internal fun reduce(state: MainUiState, event: MainUiEvent): MainUiState = when (event) {
    is MainUiEvent.AddTransaction -> {
        val balanceChanged = when (event.transaction.transactionType) {
            TransactionType.DEPOSIT -> state.balance + event.transaction.amount
            TransactionType.WITHDRAW -> state.balance - event.transaction.amount
        }
        state.copy(counter = state.counter + 1, balance = balanceChanged)
    }
}
