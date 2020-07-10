package utils

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <T> Iterable<T>.forEachParallel(f: suspend (T) -> Unit) =
    coroutineScope { map { async { f(it) } }.awaitAll() }

suspend fun <T, V> Iterable<T>.mapParallel(f: suspend (T) -> V) =
    coroutineScope { map { async { f(it) } }.awaitAll() }