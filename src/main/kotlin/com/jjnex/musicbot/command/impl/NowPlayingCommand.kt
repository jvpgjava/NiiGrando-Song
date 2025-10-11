package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import java.awt.Color
import java.util.concurrent.TimeUnit

@Component
class NowPlayingCommand(private val playerManager: PlayerManager) : Command {
    override val name = "np"
    override val description = "Mostra a música que está tocando"
    override val usage = "!np"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val musicManager = playerManager.getMusicManager(event.guild)
        val currentTrack = musicManager.player.playingTrack

        if (currentTrack == null) {
            event.channel.sendMessage("❌ Não há nenhuma música tocando!").queue()
            return
        }

        val position = formatTime(currentTrack.position)
        val duration = formatTime(currentTrack.duration)
        val percentage = (currentTrack.position.toDouble() / currentTrack.duration * 100).toInt()
        val progressBar = createProgressBar(percentage)

        val embed = EmbedBuilder()
            .setTitle("🎵 Tocando Agora")
            .setDescription("**${currentTrack.info.title}**")
            .addField("Artista", currentTrack.info.author, true)
            .addField("Duração", "$position / $duration", true)
            .addField("Progresso", progressBar, false)
            .setColor(Color.CYAN)

        if (currentTrack.info.uri != null) {
            embed.addField("Link", "[Clique aqui](${currentTrack.info.uri})", false)
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

    private fun createProgressBar(percentage: Int): String {
        val total = 20
        val filled = (percentage / 5).coerceIn(0, total)
        return "[${"█".repeat(filled)}${"░".repeat(total - filled)}] $percentage%"
    }
}

