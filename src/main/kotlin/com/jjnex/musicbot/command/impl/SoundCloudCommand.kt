package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class SoundCloudCommand(private val playerManager: PlayerManager) : Command {
    override val name = "sc"
    override val description = "Toca músicas do SoundCloud (funciona perfeitamente!)"
    override val usage = "!sc <URL ou busca>"

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

        val query = args.joinToString(" ")
        
        if (query.startsWith("http")) {
            event.channel.sendMessage("🎵 **Carregando do SoundCloud...**").queue()
        } else {
            event.channel.sendMessage("🔍 **Buscando no SoundCloud: $query**").queue()
            val searchQuery = "scsearch:$query"
            playerManager.loadAndPlay(event.channel.asTextChannel(), searchQuery)
            return
        }

        playerManager.loadAndPlay(event.channel.asTextChannel(), query)
    }
}
