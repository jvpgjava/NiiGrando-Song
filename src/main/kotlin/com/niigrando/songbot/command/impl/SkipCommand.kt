package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class SkipCommand(private val playerManager: PlayerManager) : Command {
    override val name = "skip"
    override val description = "Pula a música atual"
    override val usage = "!skip"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val musicManager = playerManager.getMusicManager(event.guild)
        
        if (musicManager.player.playingTrack == null) {
            event.channel.sendMessage("❌ Não há nenhuma música tocando!").queue()
            return
        }

        musicManager.scheduler.nextTrack()
        event.channel.sendMessage("⏭️ Música pulada!").queue()
    }
}

