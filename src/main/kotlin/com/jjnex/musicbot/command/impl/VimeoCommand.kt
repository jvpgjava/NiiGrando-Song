package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class VimeoCommand(private val playerManager: PlayerManager) : Command {
    override val name = "vimeo"
    override val description = "Toca vídeos do Vimeo com áudio"
    override val usage = "!vimeo <URL do vídeo>"

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
        
        if (query.startsWith("https://vimeo.com/")) {
            event.channel.sendMessage("🎵 **Carregando vídeo do Vimeo...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
        } else {
            event.channel.sendMessage("❌ **Para Vimeo, use um link direto:**\n" +
                "💡 Exemplo: `!vimeo https://vimeo.com/123456789`").queue()
        }
    }
}
