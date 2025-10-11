package com.jjnex.musicbot.config

import com.jjnex.musicbot.listeners.CommandListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfiguration(
    @Value("\${discord.token}") private val token: String,
    private val commandListener: CommandListener
) {
    private val logger = LoggerFactory.getLogger(BotConfiguration::class.java)

    @Bean
    fun jda(): JDA {
        logger.info("Iniciando bot Discord...")
        
        return JDABuilder.createDefault(token)
            .enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT
            )
            .addEventListeners(commandListener)
            .build()
            .awaitReady()
            .also {
                logger.info("Bot conectado como: ${it.selfUser.name}")
            }
    }
}

