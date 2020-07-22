package worker

import data.Quotes
import data.Quotes.MAX_QUOTES_SIZE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import repository.QuoteRepository


@FlowPreview
object BackgroundWorker {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val repository = QuoteRepository()

    fun initialize() {
        Quotes.setQuoteObserver { size ->
            (MAX_QUOTES_SIZE - size).takeIf { it > 0 }
                ?.let { coroutineScope.launch { loadQuotes(it) } }
        }
    }

    private suspend fun loadQuotes(amount: Int) {
        (1..amount).asFlow()
            .flatMapMerge { repository.extractQuoteFlow() }
            .flowOn(Dispatchers.IO)
            .collect { it?.let { Quotes.addQuote(it) } }
    }
}