package com.niigrando.songbot.listeners

import com.niigrando.songbot.command.Command
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CommandListener(
    @Value("\${discord.prefix}") private val prefix: String,
    private val commands: List<Command>
) : ListenerAdapter() {

    private val logger = LoggerFactory.getLogger(CommandListener::class.java)
    private val commandMap: Map<String, Command> = commands.associateBy { it.name }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        
        val message = event.message.contentRaw
        if (!message.startsWith(prefix)) return

        val args = message.substring(prefix.length).split("\\s+".toRegex())
        val commandName = args[0].lowercase()
        val commandArgs = args.drop(1)

        val command = commandMap[commandName]
        if (command != null) {
            try {
                logger.info("Executando comando: $commandName por ${event.author.name}")
                command.execute(event, commandArgs)
            } catch (e: Exception) {
                logger.error("Erro ao executar comando $commandName", e)
                event.channel.sendMessage("❌ Erro ao executar comando: ${e.message}").queue()
            }
        }
    }
}

