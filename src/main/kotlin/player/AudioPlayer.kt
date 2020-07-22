package player

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel


class AudioPlayer {

    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager().also {
        AudioSourceManagers.registerLocalSource(it)
        AudioSourceManagers.registerRemoteSources(it)
    }

    private val audioManagers: MutableMap<Long, GuildAudioManager> = HashMap()

    fun loadAndPlay(channel: TextChannel, audioUri: String) {
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

    @Synchronized
    private fun getGuildAudioManager(guild: Guild): GuildAudioManager {
        val guildId = guild.id.toLong()

        var audioManager = audioManagers[guildId]

        if (audioManager == null) {
            audioManager = GuildAudioManager(playerManager)
            audioManagers[guildId] = audioManager
        }

        guild.audioManager.sendingHandler = audioManager.getSendHandler()

        return audioManager
    }
}