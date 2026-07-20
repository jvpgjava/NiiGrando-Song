package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class SearchCommand(private val playerManager: PlayerManager) : Command {
    override val name = "search"
    override val description = "Busca e toca uma música (alternativa ao play)"
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
        val memberChannel = voiceState.channel ?: return

        if (!audioManager.isConnected) {
            audioManager.openAudioConnection(memberChannel)
        }

        val searchTerm = args.joinToString(" ")
        
        // Usar busca universal
        val searchUrl = "ytsearch:$searchTerm"

        event.channel.sendMessage("🔍 **Buscando:** `$searchTerm`\n🎵 **Plataforma:** YouTube").queue()
        playerManager.loadAndPlay(event.channel.asTextChannel(), searchUrl)
    }
}

