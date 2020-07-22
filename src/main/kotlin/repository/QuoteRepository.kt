package repository

import data.Quotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.EmbedBuilder
import network.QuoteService
import network.Service
import utils.imageUri
import utils.quote
import java.awt.Color


class QuoteRepository {

    @ExperimentalStdlibApi
    suspend fun getQuoteAsync() =
        coroutineScope {
            async {
                Quotes.quote ?: withContext(Dispatchers.IO) { extractQuote() }
            }
        }

    fun extractQuoteFlow() = flow {
        emit(extractQuote())
    }

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

    private suspend fun extractQuote() =
        try {
            Service.retrofitService.getQuote().quote
        } catch (t: Throwable) {
            null
        }
}