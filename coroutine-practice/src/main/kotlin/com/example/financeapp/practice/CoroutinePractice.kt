package com.example.financeapp.practice

import java.io.IOException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs
import kotlin.time.measureTime

private const val INCOME_AMOUNT = 4_000
private const val EXPENSES_AMOUNT = 900
private const val INCOME_DELAY_MS = 600L
private const val EXPENSES_DELAY_MS = 900L
private const val OPERATIONS_DELAY_MS = 1_000L
private const val CACHE_DELAY_MS = 700L
private const val STEP_DELAY_MS = 200L
private const val CANCEL_AFTER_MS = 450L
private const val SHORT_TIMEOUT_MS = 300L
private const val LONG_TIMEOUT_MS = 1_200L
private const val FLOW_DELAY_MS = 200L
private const val STEP_COUNT = 5
private const val EXERCISE_COUNT = 8
private const val COLLECTED_STATES = 3

private val operations = listOf(
    Operation("Доход", INCOME_AMOUNT),
    Operation("Продукты", -650),
    Operation("Кофе", -250),
)

private data class Operation(val title: String, val amount: Int) {
    override fun toString(): String = "$title: $amount ₽"
}

private suspend fun loadIncome(): Int {
    delay(INCOME_DELAY_MS)
    return INCOME_AMOUNT
}

private suspend fun loadExpenses(): Int {
    delay(EXPENSES_DELAY_MS)
    return EXPENSES_AMOUNT
}

private suspend fun loadOperations(shouldFail: Boolean = false): List<Operation> {
    delay(OPERATIONS_DELAY_MS)
    if (shouldFail) throw IOException("Учебная ошибка загрузки")
    return operations
}

private suspend fun readCachedBalance(): Int {
    println("Чтение кеша в потоке: ${Thread.currentThread().name}")
    Thread.sleep(CACHE_DELAY_MS)
    return INCOME_AMOUNT - EXPENSES_AMOUNT
}

private fun operationUpdates() = flow {
    println("Источник запущен")
    for (operation in operations) {
        delay(FLOW_DELAY_MS)
        emit(operation)
    }
}

private suspend fun loadBalance(): Int = loadIncome() - loadExpenses()

private suspend fun exercise1() {
    println("Начало")
    println("Баланс: ${loadBalance()} ₽")
    println("Готово")
}

private suspend fun exercise2() = coroutineScope {
    val job = launch {
        val income = loadIncome()
        println("Доход: $income ₽")
    }

    println("Запрос запущен")
    job.join()
    println("Все задачи завершены")
}

private suspend fun exercise3() {
    val sequentialTime = measureTime {
        val balance = loadIncome() - loadExpenses()
        println("Баланс: $balance ₽")
    }
    println("Последовательно: $sequentialTime")

    val concurrentTime = measureTime {
        val balance = coroutineScope {
            val income = async { loadIncome() }
            val expenses = async { loadExpenses() }
            income.await() - expenses.await()
        }
        println("Баланс: $balance ₽")
    }
    println("Конкурентно: $concurrentTime")
}

private suspend fun exercise4() {
    println("До чтения: ${Thread.currentThread().name}")
    val balance = withContext(Dispatchers.IO) { readCachedBalance() }
    println("После чтения: ${Thread.currentThread().name}")
    println("Баланс из кеша: $balance ₽")
}

private suspend fun exercise5() = coroutineScope {
    val job = launch {
        try {
            repeat(STEP_COUNT) { step ->
                delay(STEP_DELAY_MS)
                println("Шаг ${step + 1}")
            }
        } finally {
            println("Ресурс освобождён")
        }
    }

    delay(CANCEL_AFTER_MS)
    job.cancelAndJoin()
    println("Отмена завершена")
}

private suspend fun exercise6() {
    for (timeout in listOf(SHORT_TIMEOUT_MS, LONG_TIMEOUT_MS)) {
        val items = withTimeoutOrNull(timeout) {
            loadOperations(shouldFail = false)
        }
        if (items == null) {
            println("Время ожидания истекло")
        } else {
            println("Загружено операций: ${items.size}")
        }
    }

    val items = try {
        loadOperations(shouldFail = true)
    } catch (error: IOException) {
        println(error.message)
        loadOperations(shouldFail = false)
    }
    println("Загружено операций: ${items.size}")
}

private suspend fun exercise7() {
    val updates = operationUpdates()
    updates.collect { println(it) }

    updates
        .filter { it.amount < 0 }
        .map { "${it.title} — ${abs(it.amount)} ₽" }
        .collect { println(it) }
}

private suspend fun exercise8() = coroutineScope {
    val mutableState = MutableStateFlow("Ожидание")
    val state = mutableState.asStateFlow()

    launch(start = CoroutineStart.UNDISPATCHED) {
        state.take(COLLECTED_STATES).collect { println(it) }
    }

    mutableState.value = "Загрузка"
    val items = loadOperations(shouldFail = false)
    mutableState.value = "Загружено: ${items.size}"
}

private suspend fun runExercise(number: String) {
    when (number) {
        "demo" -> {
            for (index in 1..EXERCISE_COUNT) {
                println("\n=== Задание $index ===")
                runExercise(index.toString())
            }
        }

        "1" -> exercise1()
        "2" -> exercise2()
        "3" -> exercise3()
        "4" -> exercise4()
        "5" -> exercise5()
        "6" -> exercise6()
        "7" -> exercise7()
        "8" -> exercise8()

        else -> println("Нет задания '$number'. Используйте demo или номер от 1 до $EXERCISE_COUNT.")
    }
}

fun main(args: Array<String>) = runBlocking {
    runExercise(args.firstOrNull() ?: "demo")
}
