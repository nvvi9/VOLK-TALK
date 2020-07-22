package adapters

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    init {
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
            event.channel.sendMessage(quoteRepository.getHelpEmbedded()).queue()
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        event.takeIf { !it.author.isBot && it.message.contentRaw.startsWith('*') }?.let {
            coroutineScope.launch {
                commands.getOrDefault(it.message.contentRaw.toLowerCase()) {
                    it.channel.apply {
                        sendMessage("<@${it.author.id}>, ${quoteRepository.getWrongCommandAnswer()}").queue()
                        sendMessage(quoteRepository.getHelpEmbedded()).queue()
                    }
                }.invoke(it)
            }
        }
        super.onGuildMessageReceived(event)
    }
}