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
import utils.imageUri
import utils.quote
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
                    .setImage(Quotes.imageUri)
                    .setColor(Color.CYAN)
                    .build()
            }
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun extractQuote() =
        withContext(Dispatchers.IO) {
            try {
                RetrofitService.quoteService.getQuote().string().quote
            } catch (t: Throwable) {
                null
            }
        }
}