package com.niigrando.songbot.config

import com.niigrando.songbot.listeners.CommandListener
import moe.kyokobot.libdave.NativeDaveFactory
import moe.kyokobot.libdave.jda.LDJDADaveSessionFactory
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.audio.AudioModuleConfig
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

        // Desde 01/03/2026 o Discord exige o protocolo DAVE (E2EE) em toda conexão de
        // voz; sem isso a conexão é derrubada com close code 4017.
        val daveSessionFactory = LDJDADaveSessionFactory(NativeDaveFactory())

        return JDABuilder.createDefault(token)
            .enableIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.MESSAGE_CONTENT
            )
            .setAudioModuleConfig(
                AudioModuleConfig().withDaveSessionFactory(daveSessionFactory)
            )
            .addEventListeners(commandListener)
            .build()
            .awaitReady()
            .also {
                logger.info("Bot conectado como: ${it.selfUser.name}")
            }
    }
}

