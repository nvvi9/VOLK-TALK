package player

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager

class GuildAudioManager(manager: AudioPlayerManager) {

    private val player = manager.createPlayer()

    var scheduler = AudioScheduler(player).also {
        player.addListener(it)
    }

    fun getSendHandler() =
        AudioPlayerSendHandler(player)
}
