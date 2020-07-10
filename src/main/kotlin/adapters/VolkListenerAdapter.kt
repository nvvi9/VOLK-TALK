package adapters

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import network.Service
import player.GuildAudioManager
import utils.quote
import java.util.*
import kotlin.collections.HashMap


class VolkListenerAdapter : ListenerAdapter() {

    private val audioManagers: MutableMap<Long, GuildAudioManager> = HashMap()
    private val quoteManager = QuoteManager()
    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().also {
        AudioSourceManagers.registerLocalSource(it)
        AudioSourceManagers.registerRemoteSources(it)
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (!event.author.isBot && event.message.contentRaw.first() == '*') {
            when (event.message.contentRaw.toLowerCase()) {
                "*базарь" -> CoroutineScope(Dispatchers.Default).launch {
                    val quote =
                        quoteManager.quote ?: Service.retrofitService.getQuoteAsync().await().quote

                    val voiceChannel = event.member?.voiceState?.channel

                    voiceChannel?.let {
                        event.guild.audioManager.openAudioConnection(it)
                        loadAndPlay(event.channel, quoteManager.songUri)
                    }
                    event.channel.sendMessage(quote).queue()
                }
                "*уйди" -> CoroutineScope(Dispatchers.Default).launch {
                    event.apply {
                        if (guild.selfMember.voiceState?.channel == null || Random().nextBoolean()) {
                            guild.audioManager.closeAudioConnection()
                            channel.sendMessage(quoteManager.finalQuote).queue()
                        } else {
                            loadAndPlay(channel, quoteManager.songUri)
                            channel.sendMessage("${quoteManager.rejectDisconnectQuote} <@${author.id}>")
                                .queue()
                        }
                    }
                }
                "*подсоби" -> CoroutineScope(Dispatchers.Default).launch {
                    event.channel.sendMessage(quoteManager.embedHelp).queue()
                }
                else -> CoroutineScope(Dispatchers.Default).launch {
                    event.channel.apply {
                        sendMessage("<@${event.author.id}>, ${quoteManager.answer}").queue()
                        sendMessage(quoteManager.embedHelp).queue()
                    }
                }
            }
        }
        super.onGuildMessageReceived(event)
    }

    @Synchronized
    fun getGuildAudioManager(guild: Guild): GuildAudioManager {
        val guildId = guild.id.toLong()

        var audioManager = audioManagers[guildId]

        if (audioManager == null) {
            audioManager = GuildAudioManager(playerManager)
            audioManagers[guildId] = audioManager
        }

        guild.audioManager.sendingHandler = audioManager.getSendHandler()

        return audioManager
    }

    private fun loadAndPlay(channel: TextChannel, audioUri: String) {
        val audioManager = getGuildAudioManager(channel.guild)

        playerManager.loadItemOrdered(audioManager, audioUri, object : AudioLoadResultHandler {

            override fun loadFailed(exception: FriendlyException?) {
                println("Load failed: ${exception?.message}")
            }

            override fun trackLoaded(track: AudioTrack?) {
                track?.let {
                    println("Adding to queue: ${track.info?.title}")
                    audioManager.scheduler.queue(it)
                }
            }

            override fun noMatches() {
                channel.sendMessage("Nothing found").queue()
            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                val firstTrack = playlist?.selectedTrack ?: playlist?.tracks?.get(0)

                firstTrack?.let {
                    println("Adding to queue ${it.info.title}")
                    audioManager.scheduler.queue(it)
                }
            }
        })
    }
}