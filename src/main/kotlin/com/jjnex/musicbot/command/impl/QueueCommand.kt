package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import java.awt.Color
import java.util.concurrent.TimeUnit

@Component
class QueueCommand(private val playerManager: PlayerManager) : Command {
    override val name = "queue"
    override val description = "Mostra a fila de músicas"
    override val usage = "!queue"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val musicManager = playerManager.getMusicManager(event.guild)
        val queue = musicManager.scheduler.queue
        val currentTrack = musicManager.player.playingTrack

        if (currentTrack == null && queue.isEmpty()) {
            event.channel.sendMessage("❌ A fila está vazia!").queue()
            return
        }

        val embed = EmbedBuilder()
            .setTitle("🎵 Fila de Músicas")
            .setColor(Color.BLUE)

        if (currentTrack != null) {
            val position = formatTime(currentTrack.position)
            val duration = formatTime(currentTrack.duration)
            embed.addField(
                "▶️ Tocando Agora",
                "**${currentTrack.info.title}**\n[$position / $duration]",
                false
            )
        }

        if (queue.isNotEmpty()) {
            val queueText = StringBuilder()
            queue.forEachIndexed { index, track ->
                if (index < 10) { // Mostra apenas as próximas 10 músicas
                    queueText.append("${index + 1}. **${track.info.title}** [${formatTime(track.duration)}]\n")
                }
            }
            
            if (queue.size > 10) {
                queueText.append("\n*... e mais ${queue.size - 10} músicas*")
            }
            
            embed.addField("Próximas", queueText.toString(), false)
        }

        event.channel.sendMessageEmbeds(embed.build()).queue()
    }

    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}

