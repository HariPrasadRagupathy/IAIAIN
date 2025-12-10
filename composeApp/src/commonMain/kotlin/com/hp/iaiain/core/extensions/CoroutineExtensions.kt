package com.hp.iaiain.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * Retry a suspendable operation with exponential backoff
 */
suspend inline fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelayMillis: Long = 100,
    maxDelayMillis: Long = 1000,
    backoffMultiplier: Double = 2.0,
    operation: suspend () -> T
): Result<T> {
    var currentDelay = initialDelayMillis
    var lastException: Exception? = null

    repeat(times) {
        try {
            return Result.success(operation())
        } catch (e: Exception) {
            lastException = e
            if (it < times - 1) {
                delay(currentDelay)
                currentDelay = (currentDelay * backoffMultiplier).toLong().coerceAtMost(maxDelayMillis)
            }
        }
    }

    return Result.failure(lastException ?: Exception("Unknown error"))
}

/**
 * Create a flow that emits at regular intervals
 */
fun <T> tickerFlow(
    initialValueMillis: Long = 0,
    periodMillis: Long,
    block: suspend () -> T
): Flow<T> = flow {
    if (initialValueMillis > 0) {
        delay(initialValueMillis)
    }
    while (true) {
        emit(block())
        delay(periodMillis)
    }
}

/**
 * Safely launch coroutine catching exceptions
 */
fun CoroutineScope.launchSafe(
    onError: (Throwable) -> Unit = {},
    block: suspend () -> Unit
) {
    launch {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}

/**
 * Transform Result to another Result type (non-suspend version)
 */
inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = { value -> transform(value) },
        onFailure = { error -> Result.failure(error) }
    )
}

