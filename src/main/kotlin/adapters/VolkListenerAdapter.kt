package adapters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import player.AudioPlayer
import repository.QuoteRepository
import utils.MessageReceivedEvent
import java.util.*


@ExperimentalStdlibApi
class VolkListenerAdapter : ListenerAdapter() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val audioPlayer = AudioPlayer()
    private val quoteRepository = QuoteRepository()
    private val commands = mutableMapOf<String, MessageReceivedEvent>()
    private val channel = Channel<Pair<String, GuildMessageReceivedEvent>>(UNLIMITED)

    init {
        setCommands()
        initReceiving()
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        event.takeIf { !it.author.isBot && it.message.contentRaw.startsWith('*') }?.let {
            coroutineScope.launch {
                channel.send(it.message.contentRaw.toLowerCase() to it)
            }
        }
        super.onGuildMessageReceived(event)
    }

    private fun setCommands() {
        commands["*базарь"] = { event ->
            val quote = quoteRepository.getQuoteAsync()
            val voiceChannel = event.member?.voiceState?.channel

            voiceChannel?.let {
                event.guild.audioManager.openAudioConnection(it)
                audioPlayer.loadAndPlay(event.channel, quoteRepository.getSongUri())
            }
            event.channel.sendMessage(quote.await() ?: quoteRepository.getRejectDisconnectMessage()).queue()
        }
        commands["*уйди"] = { event ->
            event.apply {
                if (guild.selfMember.voiceState?.channel == null || Random().nextBoolean()) {
                    guild.audioManager.closeAudioConnection()
                    channel.sendMessage(quoteRepository.getLeaveVoiceChannelMessage()).queue()
                } else {
                    audioPlayer.loadAndPlay(channel, quoteRepository.getSongUri())
                    channel.sendMessage("${quoteRepository.getRejectDisconnectMessage()} <@${author.id}>")
                        .queue()
                }
            }
        }
        commands["*подсоби"] = { event ->
            event.channel.sendMessage(quoteRepository.getHelpEmbedAsync().await()).queue()
        }
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
}