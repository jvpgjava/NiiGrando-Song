package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class ResumeCommand(private val playerManager: PlayerManager) : Command {
    override val name = "resume"
    override val description = "Retoma a música pausada"
    override val usage = "!resume"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val musicManager = playerManager.getMusicManager(event.guild)
        
        if (musicManager.player.playingTrack == null) {
            event.channel.sendMessage("❌ Não há nenhuma música tocando!").queue()
            return
        }

        if (!musicManager.player.isPaused) {
            event.channel.sendMessage("▶️ A música não está pausada!").queue()
            return
        }

        musicManager.player.isPaused = false
        event.channel.sendMessage("▶️ Música retomada!").queue()
    }
}

