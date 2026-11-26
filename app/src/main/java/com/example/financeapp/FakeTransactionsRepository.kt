package com.example.financeapp

import java.io.IOException
import kotlinx.coroutines.delay

internal class FakeTransactionsRepository {
    suspend fun loadOperations(shouldFail: Boolean = false): List<Transaction> {
        delay(LOAD_DELAY_MS)
        if (shouldFail) {
            throw IOException("Учебная ошибка загрузки")
        }
        return listOf(
            Transaction(
                amount = COFFEE_AMOUNT,
                type = TransactionType.EXPENSE,
                description = COFFEE_TITLE,
            ),
        )
    }

    companion object {
        const val LOAD_DELAY_MS = 1_000L
        private const val COFFEE_AMOUNT = 250
        private const val COFFEE_TITLE = "Кофе"
    }
}
