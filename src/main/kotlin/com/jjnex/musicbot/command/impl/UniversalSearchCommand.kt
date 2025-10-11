package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import com.jjnex.musicbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class UniversalSearchCommand(private val playerManager: PlayerManager) : Command {
    override val name = "find"
    override val description = "Busca universal - encontra música em qualquer plataforma"
    override val usage = "!find <nome da música>"

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
        
        // Se for URL, toca diretamente
        if (query.startsWith("http")) {
            event.channel.sendMessage("🎵 **Carregando link direto...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
            return
        }
        
        // Lista de plataformas para tentar (em ordem de preferência)
        val platforms = listOf(
            "SoundCloud" to "scsearch:$query",
            "Bandcamp" to "bcsearch:$query"
        )
        
        event.channel.sendMessage("🔍 **🔎 BUSCA UNIVERSAL** 🔍\n" +
            "📝 **Procurando:** `$query`\n" +
            "🎯 **Tentando ${platforms.size} plataformas confiáveis...**").queue()
        
        // Tentar primeira plataforma (SoundCloud - mais confiável)
        val firstPlatform = platforms.first()
        event.channel.sendMessage("🎵 **Tentativa 1/2:** ${firstPlatform.first}").queue()
        
        playerManager.loadAndPlayWithFallback(event.channel.asTextChannel(), firstPlatform.second, platforms, 0)
    }
}
