package repository

import data.Quotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.EmbedBuilder
import network.Service
import utils.quote


class QuoteRepository {

    @ExperimentalStdlibApi
    suspend fun getQuoteAsync() =
        coroutineScope {
            async {
                Quotes.quote ?: withContext(Dispatchers.IO) { extractQuote() }
            }
        }

    private suspend fun extractQuote() =
        try {
            Service.retrofitService.getQuote().quote
        } catch (t: Throwable) {
            null
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

    fun getHelpEmbedded() =
        EmbedBuilder().setDescription(Quotes.helpText).build()
}