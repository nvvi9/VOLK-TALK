package player

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

class AudioPlayerSendHandler(private val player: AudioPlayer) : AudioSendHandler {

    private val buffer = ByteBuffer.allocate(1024)
    private val frame = MutableAudioFrame().apply {
        setBuffer(buffer)
    }

    override fun provide20MsAudio(): ByteBuffer? =
        buffer.apply { flip() }

    override fun canProvide(): Boolean =
        player.provide(frame)

    override fun isOpus(): Boolean = true
}
