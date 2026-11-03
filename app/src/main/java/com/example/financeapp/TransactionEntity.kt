package com.example.financeapp

import android.app.ActivityManager
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.temporal.TemporalAmount

@Entity(tableName = "transactions")
internal data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val type: TransactionType,
    val description: String
) {
    fun toTransaction(): Transaction =
        Transaction(amount = amount, type = type, description = description)

    companion object {
        fun fromTransaction(transaction: Transaction): TransactionEntity =
            TransactionEntity(
                amount = transaction.amount,
                type = transaction.type,
                description = transaction.description
            )
    }
}