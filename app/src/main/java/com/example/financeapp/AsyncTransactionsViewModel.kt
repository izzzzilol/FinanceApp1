package com.example.financeapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import kotlinx.coroutines.launch

internal data class AsyncUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val operations: List<Transaction> = emptyList(),
)

internal class AsyncTransactionsViewModel : ViewModel() {
    private val repository = FakeTransactionsRepository()

    var uiState by mutableStateOf(AsyncUiState())
        private set

    fun load(shouldFail: Boolean) {
        viewModelScope.launch {
            uiState = AsyncUiState(isLoading = true)
            try {
                val operations = repository.loadOperations(shouldFail = shouldFail)
                uiState = AsyncUiState(operations = operations)
            } catch (expected: IOException) {
                uiState = AsyncUiState(isError = true)
            }
        }
    }
}
