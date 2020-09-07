package repository

import data.Quotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.EmbedBuilder
import network.RetrofitService
import java.awt.Color

class QuoteRepository {

    @ExperimentalStdlibApi
    suspend fun getQuote() =
        coroutineScope {
            Quotes.quote ?: extractQuote()
        }

    fun extractQuoteFlow() = flow {
        emit(extractQuote())
    }.flowOn(Dispatchers.IO)

    fun getWrongCommandAnswer() =
        Quotes.wrongCommandAnswer

    fun getLeaveVoiceChannelMessage() =
        Quotes.leaveVoiceChannelMessage

    fun getSongUri() =
        Quotes.songUri

    fun getRejectDisconnectMessage() =
        Quotes.rejectDisconnectionMessage

    suspend fun getHelpEmbedAsync() =
        coroutineScope {
            async {
                EmbedBuilder()
                    .setDescription(Quotes.helpText)
                    .setColor(Color.CYAN)
                    .build()
            }
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun extractQuote() =
        withContext(Dispatchers.IO) {
            try {
                RetrofitService.quoteService.getQuote().string().asQuote()
            } catch (t: Throwable) {
                null
            }
        }

    private fun String.asQuote() = substringAfter("<div class=\"text\">")
        .substringBefore("</div><a class=")
        .takeIf { it.isNotEmpty() }
}
