package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class SearchCommand(private val playerManager: PlayerManager) : Command {
    override val name = "search"
    override val description = "Busca e toca uma música no YouTube (alternativa ao play)"
    override val usage = "!search <termo de busca>"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            event.channel.sendMessage("❌ Uso correto: $usage").queue()
            return
        }

        val member = event.member ?: return
        val voiceState = member.voiceState

        if (voiceState?.inAudioChannel() != true) {
            event.channel.sendMessage("❌ Você precisa estar em um canal de voz!").queue()
            return
        }

        val audioManager = event.guild.audioManager
        val memberChannel = voiceState.channel

        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(memberChannel)
        }

        val searchTerm = args.joinToString(" ")
        
        // Usar ytsearch para busca no YouTube
        val searchUrl = "ytsearch:$searchTerm"
        
        event.channel.sendMessage("🔍 Buscando: **$searchTerm**").queue()
        playerManager.loadAndPlay(event.channel.asTextChannel(), searchUrl)
    }
}

