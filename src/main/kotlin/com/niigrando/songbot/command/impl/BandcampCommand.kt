package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class BandcampCommand(private val playerManager: PlayerManager) : Command {
    override val name = "bandcamp"
    override val description = "Toca músicas do Bandcamp"
    override val usage = "!bandcamp <URL ou busca>"

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

        val query = args.joinToString(" ")
        
        if (query.startsWith("https://") && query.contains("bandcamp.com")) {
            event.channel.sendMessage("🎵 **Carregando do Bandcamp...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
        } else {
            event.channel.sendMessage("🔍 **Buscando no Bandcamp: $query**").queue()
            // Para busca no Bandcamp, usamos scsearch como fallback
            val searchQuery = "scsearch:$query"
            playerManager.loadAndPlay(event.channel.asTextChannel(), searchQuery)
        }
    }
}
