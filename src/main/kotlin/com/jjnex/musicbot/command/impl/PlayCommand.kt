package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class PlayCommand(private val playerManager: PlayerManager) : Command {
    override val name = "play"
    override val description = "Toca uma música pelo URL ou pesquisa universal"
    override val usage = "!play <url ou termo de busca>"

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
        
        // Se for URL direto, toca diretamente
        if (query.startsWith("http")) {
            event.channel.sendMessage("🎵 **Carregando link direto...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
            return
        }
        
        // Para busca, usar busca universal
        event.channel.sendMessage("🔍 **🔎 BUSCA UNIVERSAL** 🔍\n" +
            "📝 **Procurando:** `$query`\n" +
            "🎯 **Tentando múltiplas plataformas...**").queue()
        
        // Lista de plataformas para tentar (apenas as que funcionam bem)
        val platforms = listOf(
            "SoundCloud" to "scsearch:$query",
            "Bandcamp" to "bcsearch:$query"
        )
        
        event.channel.sendMessage("🎵 **Tentativa 1/2:** SoundCloud").queue()
        playerManager.loadAndPlayWithFallback(event.channel.asTextChannel(), platforms.first().second, platforms, 0)
    }
}

