package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class TwitchCommand(private val playerManager: PlayerManager) : Command {
    override val name = "twitch"
    override val description = "Toca streams do Twitch"
    override val usage = "!twitch <URL do stream>"

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
        
        if (query.startsWith("https://www.twitch.tv/")) {
            event.channel.sendMessage("🎵 **Carregando stream do Twitch...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
        } else {
            event.channel.sendMessage("❌ **Para Twitch, use um link direto:**\n" +
                "💡 Exemplo: `!twitch https://www.twitch.tv/username`").queue()
        }
    }
}
