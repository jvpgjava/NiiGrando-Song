package com.niigrando.songbot.command.impl

import com.niigrando.songbot.command.Command
import com.niigrando.songbot.service.PlayerManager
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class FileCommand(private val playerManager: PlayerManager) : Command {
    override val name = "file"
    override val description = "Toca arquivos de música locais"
    override val usage = "!file <caminho do arquivo>"

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
        
        // Verificar se é um arquivo local ou URL de arquivo
        if (query.startsWith("/") || query.startsWith("C:") || query.startsWith("D:") || 
            query.startsWith("file://") || query.endsWith(".mp3") || query.endsWith(".wav") || 
            query.endsWith(".flac") || query.endsWith(".ogg") || query.endsWith(".m4a")) {
            
            event.channel.sendMessage("🎵 **Carregando arquivo de música...**").queue()
            playerManager.loadAndPlay(event.channel.asTextChannel(), query)
        } else {
            event.channel.sendMessage("❌ **Para arquivos, use um caminho válido:**\n" +
                "💡 Exemplos:\n" +
                "• `!file /caminho/para/musica.mp3`\n" +
                "• `!file C:\\Musicas\\musica.mp3`\n" +
                "• `!file file://caminho/para/musica.wav`").queue()
        }
    }
}
