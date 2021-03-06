package player

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class AudioScheduler(private val player: AudioPlayer) : AudioEventAdapter() {

    private val queue: BlockingQueue<AudioTrack> = LinkedBlockingQueue<AudioTrack>()

    fun queue(track: AudioTrack) {
        if (!player.startTrack(track, false)) {
            queue.offer(track)
        }
    }

    fun nextTrack() {
        player.startTrack(queue.poll(), false)
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        endReason?.let {
            if (it.mayStartNext) {
                nextTrack()
            }
        }
    }
}
