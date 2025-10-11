package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class StopCommand(private val playerManager: PlayerManager) : Command {
    override val name = "stop"
    override val description = "Para a música e limpa a fila"
    override val usage = "!stop"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val musicManager = playerManager.getMusicManager(event.guild)
        
        musicManager.scheduler.clearQueue()
        musicManager.player.stopTrack()
        event.guild.audioManager.closeAudioConnection()
        
        event.channel.sendMessage("⏹️ Música parada e fila limpa!").queue()
    }
}

