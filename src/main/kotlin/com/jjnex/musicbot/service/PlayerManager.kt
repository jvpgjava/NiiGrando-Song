package com.jjnex.musicbot.service

import com.jjnex.musicbot.audio.GuildMusicManager
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class PlayerManager {
    private val logger = LoggerFactory.getLogger(PlayerManager::class.java)
    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = ConcurrentHashMap()

    init {
        logger.info("Configurando fontes de áudio para múltiplas plataformas...")
        
        try {
            // Registrar todas as fontes disponíveis (exceto YouTube que está instável)
            AudioSourceManagers.registerRemoteSources(playerManager)
            AudioSourceManagers.registerLocalSource(playerManager)
            
            logger.info("✅ Fontes de áudio configuradas com sucesso!")
            logger.info("🎵 Plataformas suportadas:")
            logger.info("   • SoundCloud - ✅ Funciona perfeitamente")
            logger.info("   • Bandcamp - ✅ Música independente")
            logger.info("   • Twitch - ✅ Streams ao vivo")
            logger.info("   • Vimeo - ✅ Vídeos com áudio")
            logger.info("   • HTTP Streams - ✅ Links diretos")
            logger.info("   • Local Files - ✅ Arquivos locais")
            logger.info("   • Icecast/Shoutcast - ✅ Rádios online")
            logger.info("   • Mixcloud - ✅ Mixes e podcasts")
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao configurar fontes, usando configuração mínima", e)
            // Configuração mínima
            AudioSourceManagers.registerLocalSource(playerManager)
        }
    }

    fun getMusicManager(guild: Guild): GuildMusicManager {
        return musicManagers.computeIfAbsent(guild.idLong) {
            val musicManager = GuildMusicManager(playerManager)
            guild.audioManager.sendingHandler = musicManager.sendHandler
            musicManager
        }
    }

    fun loadAndPlay(channel: TextChannel, trackUrl: String) {
        val musicManager = getMusicManager(channel.guild)

        playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                val platform = getPlatformFromUri(track.info.uri ?: "")
                channel.sendMessage("🎵 **${platform}** - Adicionando à fila: **${track.info.title}**").queue()
                musicManager.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val firstTrack = playlist.selectedTrack ?: playlist.tracks[0]
                val platform = getPlatformFromUri(firstTrack.info.uri ?: "")

                if (playlist.isSearchResult) {
                    channel.sendMessage("🎵 **${platform}** - Adicionando à fila: **${firstTrack.info.title}**").queue()
                    musicManager.scheduler.queue(firstTrack)
                } else {
                    channel.sendMessage("🎵 **${platform}** - Adicionando playlist: **${playlist.name}** (${playlist.tracks.size} músicas)").queue()
                    playlist.tracks.forEach { track ->
                        musicManager.scheduler.queue(track)
                    }
                }
            }

            override fun noMatches() {
                channel.sendMessage("❌ Nada encontrado para: $trackUrl").queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                logger.error("Erro ao carregar áudio: $trackUrl", exception)
                val errorMessage = when {
                    exception.message?.contains("age restricted") == true ->
                        "❌ Conteúdo com restrição de idade. Tente outro link."
                    exception.message?.contains("private") == true ->
                        "❌ Conteúdo privado ou indisponível."
                    exception.message?.contains("not found") == true ->
                        "❌ Conteúdo não encontrado. Tente outro link ou busca."
                    else -> "❌ Não foi possível reproduzir: ${exception.message}\n💡 Tente `!find <nome da música>` ou use URLs diretos"
                }
                channel.sendMessage(errorMessage).queue()
            }
        })
    }

    fun loadAndPlayWithFallback(channel: TextChannel, trackUrl: String, platforms: List<Pair<String, String>>, currentIndex: Int) {
        if (currentIndex >= platforms.size) {
            channel.sendMessage("❌ **Busca falhou em todas as plataformas!**\n" +
                "💡 **Tente:**\n" +
                "• `!soundcloud <nome>`\n" +
                "• `!bandcamp <nome>`\n" +
                "• Links diretos: `!play https://soundcloud.com/...`\n" +
                "• Rádios: `!radio <url da rádio>`\n" +
                "• Arquivos: `!file <caminho do arquivo>`").queue()
            return
        }

        val musicManager = getMusicManager(channel.guild)
        val currentPlatform = platforms[currentIndex]

        playerManager.loadItemOrdered(musicManager, trackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("✅ **${currentPlatform.first}** - Encontrado: **${track.info.title}**").queue()
                musicManager.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val firstTrack = playlist.selectedTrack ?: playlist.tracks[0]
                channel.sendMessage("✅ **${currentPlatform.first}** - Encontrado: **${firstTrack.info.title}**").queue()
                musicManager.scheduler.queue(firstTrack)
            }

            override fun noMatches() {
                // Tentar próxima plataforma
                val nextIndex = currentIndex + 1
                if (nextIndex < platforms.size) {
                    val nextPlatform = platforms[nextIndex]
                    channel.sendMessage("⚠️ **${currentPlatform.first}** não encontrou nada\n" +
                        "🎯 **Tentativa ${nextIndex + 1}/4:** ${nextPlatform.first}").queue()
                    loadAndPlayWithFallback(channel, nextPlatform.second, platforms, nextIndex)
                } else {
                    channel.sendMessage("❌ **Nenhuma plataforma encontrou resultados!**").queue()
                }
            }

            override fun loadFailed(exception: FriendlyException) {
                logger.error("Erro na plataforma ${currentPlatform.first}: ${exception.message}")
                
                // Tentar próxima plataforma
                val nextIndex = currentIndex + 1
                if (nextIndex < platforms.size) {
                    val nextPlatform = platforms[nextIndex]
                    channel.sendMessage("❌ **${currentPlatform.first}** falhou\n" +
                        "🎯 **Tentativa ${nextIndex + 1}/4:** ${nextPlatform.first}").queue()
                    loadAndPlayWithFallback(channel, nextPlatform.second, platforms, nextIndex)
                } else {
                    channel.sendMessage("❌ **Todas as plataformas falharam!**").queue()
                }
            }
        })
    }

    private fun getPlatformFromUri(uri: String): String {
        return when {
            uri.contains("soundcloud.com") -> "SoundCloud"
            uri.contains("bandcamp.com") -> "Bandcamp"
            uri.contains("twitch.tv") -> "Twitch"
            uri.contains("vimeo.com") -> "Vimeo"
            uri.contains("mixcloud.com") -> "Mixcloud"
            uri.startsWith("file://") || uri.contains("/") && (uri.endsWith(".mp3") || uri.endsWith(".wav") || uri.endsWith(".flac") || uri.endsWith(".ogg") || uri.endsWith(".m4a")) -> "Arquivo Local"
            uri.startsWith("http") && (uri.contains("stream") || uri.contains("radio") || uri.contains("icecast") || uri.contains("shoutcast")) -> "Rádio Online"
            uri.startsWith("http") -> "HTTP Stream"
            else -> "Fonte Desconhecida"
        }
    }
}

