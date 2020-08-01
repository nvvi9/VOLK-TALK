package adapters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import player.VolkAudioPlayer
import repository.QuoteRepository
import utils.MessageReceivedEvent
import java.util.*


@ExperimentalStdlibApi
class VolkListenerAdapter : ListenerAdapter() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val volkAudioPlayer = VolkAudioPlayer()
    private val quoteRepository = QuoteRepository()
    private val channel = Channel<Pair<String, GuildMessageReceivedEvent>>(UNLIMITED)
    private val commands = mutableMapOf<String, MessageReceivedEvent>(
        "базарь" to { event ->
            val quote = coroutineScope.async { quoteRepository.getQuote() }
            val voiceChannel = event.member?.voiceState?.channel
            voiceChannel?.let {
                event.guild.audioManager.openAudioConnection(it)
                volkAudioPlayer.loadAndPlay(event.channel, quoteRepository.getSongUri())
            }

            event.channel.sendMessage(quote.await() ?: quoteRepository.getRejectDisconnectMessage()).queue()
        },
        "уйди" to { event ->
            event.apply {
                if (guild.selfMember.voiceState?.channel == null || Random().nextBoolean()) {
                    guild.audioManager.closeAudioConnection()
                    channel.sendMessage(quoteRepository.getLeaveVoiceChannelMessage()).queue()
                } else {
                    volkAudioPlayer.loadAndPlay(channel, quoteRepository.getSongUri())
                    channel.sendMessage("${quoteRepository.getRejectDisconnectMessage()} <@${author.id}>")
                        .queue()
                }
            }
        },
        "подсоби" to { event ->
            event.channel.sendMessage(quoteRepository.getHelpEmbedAsync().await()).queue()
        }
    )

    init {
        initReceiving()
    }

    private fun initReceiving() {
        coroutineScope.launch {
            for (request in channel) {
                commands.getOrDefault(request.first) {
                    it.channel.apply {
                        val helpMessage = quoteRepository.getHelpEmbedAsync()
                        sendMessage("<@${it.author.id}>, ${quoteRepository.getWrongCommandAnswer()}").queue()
                        sendMessage(helpMessage.await()).queue()
                    }
                }.invoke(request.second)
            }
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        event.takeUnless {
            it.author.isBot
        }?.message?.contentRaw?.replace(" ", "")?.toLowerCase()?.takeIf {
            it.startsWith('*')
        }?.removePrefix("*")?.let {
            coroutineScope.launch {
                channel.send(it to event)
            }
        }
        super.onGuildMessageReceived(event)
    }
}