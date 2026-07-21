package com.niigrando.songbot.service

import com.niigrando.songbot.audio.GuildMusicManager
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.AndroidVr
import dev.lavalink.youtube.clients.Music
import dev.lavalink.youtube.clients.Tv
import dev.lavalink.youtube.clients.Web
import dev.lavalink.youtube.clients.WebEmbedded
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

@Service
class PlayerManager(
    @Value("\${youtube.oauth-refresh-token:}") private val youtubeOauthRefreshToken: String,
    @Value("\${ytdlp.resolver-url:}") private val ytdlpResolverUrl: String
) {
    private val logger = LoggerFactory.getLogger(PlayerManager::class.java)
    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = ConcurrentHashMap()

    init {
        logger.info("Configurando fontes de áudio para múltiplas plataformas...")

        try {
            // Registrado ANTES de registerRemoteSources() para ter prioridade sobre o
            // YoutubeAudioSourceManager nativo do LavaPlayer, que está quebrado (bloqueado
            // pelo YouTube). Este simula múltiplos clientes (Music, Android VR, Web) para
            // contornar o bloqueio.
            // TV é o único cliente compatível com OAuth2 (ver
            // https://github.com/lavalink-devs/youtube-source#available-clients). Fica por
            // último porque sem token ele também exige login, então só compensa como fallback.
            val youtube = YoutubeAudioSourceManager(
                true,
                Music(), AndroidVr(), Web(), WebEmbedded(), Tv()
            )
            if (youtubeOauthRefreshToken.isNotBlank()) {
                youtube.useOauth2(youtubeOauthRefreshToken, true)
                logger.info("   • YouTube OAuth2 - ✅ Configurado com token fornecido")
            } else {
                logger.warn("   • YouTube OAuth2 - ⚠️ Sem token configurado. Muitos vídeos vão pedir login " +
                    "(comum em IPs de VPS/datacenter). Iniciando fluxo de autorização abaixo - siga as " +
                    "instruções para gerar um token e configure 'youtube.oauth-refresh-token'.")
                youtube.useOauth2(null, false)
            }
            playerManager.registerSourceManager(youtube)

            // Registrar as demais fontes disponíveis (plataformas confiáveis)
            AudioSourceManagers.registerRemoteSources(playerManager)
            AudioSourceManagers.registerLocalSource(playerManager)

            logger.info("✅ Fontes de áudio configuradas com sucesso!")
            logger.info("🎵 Plataformas suportadas:")
            logger.info("   • YouTube - ✅ Vídeos, músicas e playlists")
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
                val requiresLogin = exception.message?.contains("requires login", ignoreCase = true) == true ||
                    exception.message?.contains("Sign in", ignoreCase = true) == true
                val errorMessage = when {
                    exception.message?.contains("age restricted") == true ->
                        "❌ Conteúdo com restrição de idade. Tente outro link."
                    exception.message?.contains("private") == true ->
                        "❌ Conteúdo privado ou indisponível."
                    exception.message?.contains("not found") == true ->
                        "❌ Conteúdo não encontrado. Tente outro link ou busca."
                    requiresLogin ->
                        "❌ O YouTube está pedindo login para este vídeo.\n" +
                            "💡 Configure `youtube.oauth-refresh-token` no `application.yml` (veja o README)."
                    else -> "❌ Não foi possível reproduzir: ${exception.message}\n💡 Tente `!find <nome da música>` ou use URLs diretos"
                }

                val isYoutubeUrl = trackUrl.contains("youtube.com") || trackUrl.contains("youtu.be")
                if (requiresLogin && isYoutubeUrl && ytdlpResolverUrl.isNotBlank()) {
                    tryYtDlpFallback(channel, musicManager, trackUrl, errorMessage)
                } else {
                    channel.sendMessage(errorMessage).queue()
                }
            }
        })
    }

    private fun tryYtDlpFallback(channel: TextChannel, musicManager: GuildMusicManager, originalUrl: String, originalErrorMessage: String) {
        channel.sendMessage("🔄 YouTube pediu login para esse vídeo, tentando via yt-dlp...").queue()

        val encodedUrl = URLEncoder.encode(originalUrl, "UTF-8")
        val resolverTrackUrl = "$ytdlpResolverUrl/resolve?url=$encodedUrl"

        playerManager.loadItemOrdered(musicManager, resolverTrackUrl, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                channel.sendMessage("🎵 **YouTube (via yt-dlp)** - Adicionando à fila").queue()
                musicManager.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                val firstTrack = playlist.selectedTrack ?: playlist.tracks.firstOrNull()
                if (firstTrack == null) {
                    channel.sendMessage(originalErrorMessage).queue()
                    return
                }
                channel.sendMessage("🎵 **YouTube (via yt-dlp)** - Adicionando à fila").queue()
                musicManager.scheduler.queue(firstTrack)
            }

            override fun noMatches() {
                channel.sendMessage(originalErrorMessage).queue()
            }

            override fun loadFailed(exception: FriendlyException) {
                logger.error("Fallback yt-dlp também falhou para: $originalUrl", exception)
                channel.sendMessage(originalErrorMessage).queue()
            }
        })
    }

    fun loadAndPlayWithFallback(channel: TextChannel, trackUrl: String, platforms: List<Pair<String, String>>, currentIndex: Int) {
        if (currentIndex >= platforms.size) {
            channel.sendMessage("❌ **Busca falhou em todas as plataformas!**\n" +
                "💡 **Tente:**\n" +
                "• Links diretos: `!play https://youtube.com/...`\n" +
                "• `!soundcloud <nome>`\n" +
                "• `!bandcamp <nome>`\n" +
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
            uri.contains("youtube.com") || uri.contains("youtu.be") -> "YouTube"
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

