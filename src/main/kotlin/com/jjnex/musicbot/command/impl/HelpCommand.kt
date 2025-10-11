package com.jjnex.musicbot.command.impl

import com.jjnex.musicbot.command.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.awt.Color

@Component
class HelpCommand(
    @Value("\${discord.prefix}") private val prefix: String,
    private val commands: List<Command>
) : Command {
    override val name = "help"
    override val description = "Mostra todos os comandos disponíveis"
    override val usage = "!help"

    override fun execute(event: MessageReceivedEvent, args: List<String>) {
        val embed = EmbedBuilder()
            .setTitle("🎵 Comandos do Bot de Música")
            .setDescription("Prefixo: `$prefix`")
            .setColor(Color.GREEN)

        // Agrupar comandos por categoria
        val platformCommands = commands.filter { it.name in listOf("play", "find", "soundcloud", "bandcamp", "twitch", "vimeo", "mixcloud", "radio", "file") }
        val controlCommands = commands.filter { it.name in listOf("pause", "resume", "skip", "stop", "queue", "np") }
        val otherCommands = commands.filter { it.name !in listOf("play", "find", "soundcloud", "bandcamp", "twitch", "vimeo", "mixcloud", "radio", "file", "pause", "resume", "skip", "stop", "queue", "np") }
        
        embed.addField("🎵 **Comandos de Música**", "", false)
        platformCommands.forEach { command ->
            embed.addField(
                "$prefix${command.name}",
                "${command.description}\n`${command.usage}`",
                false
            )
        }
        
        embed.addField("🎮 **Controles de Player**", "", false)
        controlCommands.forEach { command ->
            embed.addField(
                "$prefix${command.name}",
                "${command.description}\n`${command.usage}`",
                false
            )
        }
        
        embed.addField("ℹ️ **Outros Comandos**", "", false)
        otherCommands.forEach { command ->
            embed.addField(
                "$prefix${command.name}",
                "${command.description}\n`${command.usage}`",
                false
            )
        }
        
        embed.addField("💡 **Dicas**", 
            "• Use `!find` para busca universal em plataformas confiáveis\n" +
            "• Use `!play` para busca automática\n" +
            "• SoundCloud e Bandcamp funcionam perfeitamente\n" +
            "• Para URLs diretos, use `!play <url>`", false)

        event.channel.sendMessageEmbeds(embed.build()).queue()
    }
}

