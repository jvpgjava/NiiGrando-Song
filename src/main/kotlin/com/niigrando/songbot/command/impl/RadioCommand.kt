package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class RadioCommand(private val playerManager: PlayerManager) : Command {
    override val name = "radio"
    override val description = "Toca rádios online (Icecast/Shoutcast)"
    override val usage = "!radio <URL da rádio>"

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
        
        if (query.startsWith("http")) {
            event.channel.sendMessage("📻 **Carregando rádio online...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
        } else {
            event.channel.sendMessage("❌ **Para rádio, use um link direto:**\n" +
                "💡 Exemplo: `!radio http://stream.example.com:8000/stream`").queue()
        }
    }
}
