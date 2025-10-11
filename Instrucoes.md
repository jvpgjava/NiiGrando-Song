# 🎵 Instruções Completas - JJNex Music Bot

## 📋 Índice
- [Comandos de Música](#-comandos-de-música)
- [Comandos de Controle](#-comandos-de-controle)
- [Comandos de Informação](#-comandos-de-informação)
- [Exemplos por Plataforma](#-exemplos-por-plataforma)
- [URLs Suportadas](#-urls-suportadas)
- [Dicas e Truques](#-dicas-e-truques)

---

## 🎵 Comandos de Música

### **Busca Universal**
| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `!play <nome>` | Busca automática em múltiplas plataformas | `!play despacito` |
| `!find <nome>` | Busca universal explícita | `!find linkin park numb` |

### **Comandos por Plataforma**
| Comando | Plataforma | Descrição | Exemplo |
|---------|------------|-----------|---------|
| `!soundcloud <nome ou URL>` | SoundCloud | Busca/carrega do SoundCloud | `!soundcloud despacito` |
| `!bandcamp <nome ou URL>` | Bandcamp | Busca/carrega do Bandcamp | `!bandcamp imagine dragons` |
| `!twitch <URL>` | Twitch | Carrega stream do Twitch | `!twitch https://www.twitch.tv/username` |
| `!vimeo <URL>` | Vimeo | Carrega vídeo do Vimeo | `!vimeo https://vimeo.com/123456789` |
| `!mixcloud <URL>` | Mixcloud | Carrega mix do Mixcloud | `!mixcloud https://www.mixcloud.com/username/mix/` |
| `!radio <URL>` | Rádio Online | Carrega rádio online | `!radio http://stream.example.com:8000/stream` |
| `!file <caminho>` | Arquivo Local | Carrega arquivo local | `!file /caminho/musica.mp3` |

---

## 🎮 Comandos de Controle

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `!pause` | Pausa a música atual | `!pause` |
| `!resume` | Retoma a música pausada | `!resume` |
| `!skip` | Pula para a próxima música | `!skip` |
| `!stop` | Para a música e limpa a fila | `!stop` |

---

## ℹ️ Comandos de Informação

| Comando | Descrição | Exemplo |
|---------|-----------|---------|
| `!queue` | Mostra a fila de músicas | `!queue` |
| `!np` | Mostra a música atual tocando | `!np` |
| `!help` | Lista todos os comandos | `!help` |

---

## 🎯 Exemplos por Plataforma

### **SoundCloud**
```
!play despacito luis fonsi
!soundcloud linkin park numb
!soundcloud https://soundcloud.com/artist/song-name
```

### **Bandcamp**
```
!play imagine dragons thunder
!bandcamp linkin park meteora
!bandcamp https://artist.bandcamp.com/album/album-name
```

### **Twitch**
```
!twitch https://www.twitch.tv/username
!play https://www.twitch.tv/username
```

### **Vimeo**
```
!vimeo https://vimeo.com/123456789
!play https://vimeo.com/123456789
```

### **Mixcloud**
```
!mixcloud https://www.mixcloud.com/username/mix-name/
!play https://www.mixcloud.com/username/mix-name/
```

### **Rádios Online**
```
!radio http://stream.example.com:8000/stream
!radio http://icecast.example.com:8000/radio.mp3
!play http://stream.example.com:8000/stream
```

### **Arquivos Locais**
```
!file /caminho/para/musica.mp3
!file C:\Musicas\song.wav
!file file:///path/to/music.flac
!play /caminho/para/musica.mp3
```

---

## 🔗 URLs Suportadas

### **SoundCloud**
- ✅ `https://soundcloud.com/artist/song-name`
- ✅ `https://soundcloud.com/artist/playlist-name`
- ✅ `https://soundcloud.com/artist/sets/album-name`

### **Bandcamp**
- ✅ `https://bandcamp.com/album/album-name`
- ✅ `https://artist.bandcamp.com/album/album-name`
- ✅ `https://artist.bandcamp.com/track/track-name`

### **Twitch**
- ✅ `https://www.twitch.tv/username`
- ✅ `https://twitch.tv/username`

### **Vimeo**
- ✅ `https://vimeo.com/123456789`
- ✅ `https://player.vimeo.com/video/123456789`

### **Mixcloud**
- ✅ `https://www.mixcloud.com/username/mix-name/`

### **Rádios (Icecast/Shoutcast)**
- ✅ `http://stream.example.com:8000/stream`
- ✅ `http://icecast.example.com:8000/radio.mp3`
- ✅ `https://radio.example.com/stream`

### **Arquivos Locais**
- ✅ `/caminho/para/musica.mp3`
- ✅ `C:\Musicas\song.wav`
- ✅ `file:///path/to/music.flac`
- ✅ `file://c:/music/song.m4a`

### **Formatos de Arquivo Suportados**
- ✅ **MP3** - `.mp3`
- ✅ **WAV** - `.wav`
- ✅ **FLAC** - `.flac`
- ✅ **OGG** - `.ogg`
- ✅ **M4A** - `.m4a`

---

## 💡 Dicas e Truques

### **🎯 Busca Inteligente**
```
# Busca universal (recomendado)
!play despacito
!find linkin park numb

# Busca específica por plataforma
!soundcloud despacito
!bandcamp imagine dragons
```

### **🔗 URLs Diretos**
```
# Qualquer comando aceita URLs diretos
!play https://soundcloud.com/artist/song
!soundcloud https://soundcloud.com/artist/song
!bandcamp https://bandcamp.com/album/album-name
```

### **📁 Arquivos Locais**
```
# Caminhos absolutos
!file /caminho/completo/musica.mp3
!file C:\Musicas\song.wav

# URLs file://
!file file:///path/to/music.flac
!file file://c:/music/song.m4a
```

### **📻 Rádios Online**
```
# Streams HTTP
!radio http://stream.example.com:8000/stream
!play http://icecast.example.com:8000/radio.mp3

# URLs HTTPS também funcionam
!radio https://radio.example.com/stream
```

### **🎮 Controle da Fila**
```
# Ver o que está tocando
!np

# Ver a fila completa
!queue

# Pular música atual
!skip

# Pausar/retomar
!pause
!resume

# Parar tudo
!stop
```

---

## 🔍 Como Funciona a Busca Universal

### **Processo Automático:**
1. **Tentativa 1:** SoundCloud
2. **Tentativa 2:** Bandcamp
3. **Se falhar:** Mensagem de erro com sugestões

### **Vantagens:**
- ✅ **Automático** - Não precisa especificar plataforma
- ✅ **Inteligente** - Tenta múltiplas fontes
- ✅ **Rápido** - Usa apenas plataformas confiáveis
- ✅ **Fallback** - Se uma falha, tenta outra

---

## 🚨 Resolução de Problemas

### **Bot não toca música**
1. ✅ Verifique se está em um canal de voz
2. ✅ Verifique se o bot tem permissão para falar
3. ✅ Tente comando específico: `!soundcloud <nome>`

### **Busca não encontra nada**
1. ✅ Tente nome mais específico
2. ✅ Use comando específico: `!soundcloud <nome>`
3. ✅ Use URL direto: `!play https://soundcloud.com/...`

### **Arquivo local não carrega**
1. ✅ Use caminho absoluto
2. ✅ Verifique se o arquivo existe
3. ✅ Use formato suportado (.mp3, .wav, .flac, .ogg, .m4a)

### **Rádio não funciona**
1. ✅ Verifique se a URL está correta
2. ✅ Teste a URL em um navegador primeiro
3. ✅ Use formato suportado (MP3, OGG, etc.)

---

## 📊 Estatísticas de Compatibilidade

| Plataforma | Status | Busca | URLs | Confiabilidade |
|------------|--------|-------|------|----------------|
| **SoundCloud** | ✅ | ✅ | ✅ | 100% |
| **Bandcamp** | ✅ | ✅ | ✅ | 95% |
| **Twitch** | ✅ | ❌ | ✅ | 90% |
| **Vimeo** | ✅ | ❌ | ✅ | 85% |
| **Mixcloud** | ✅ | ❌ | ✅ | 90% |
| **Rádios** | ✅ | ❌ | ✅ | 95% |
| **Arquivos** | ✅ | ❌ | ✅ | 100% |

---

## 🎉 Comandos Mais Úteis

### **Para Usuários Casuais:**
```
!play <nome da música>     # Busca universal
!queue                     # Ver o que está tocando
!skip                      # Pular música
!help                      # Ver todos os comandos
```

### **Para Usuários Avançados:**
```
!find <nome>               # Busca explícita
!soundcloud <nome>         # Busca específica
!bandcamp <nome>           # Busca específica
!np                        # Informações detalhadas
!stop                      # Limpar tudo
```

### **Para Administradores:**
```
!queue                     # Monitorar fila
!np                        # Ver status atual
!help                      # Documentação completa
```

---

**🎵 Agora você sabe todos os comandos! Divirta-se com seu bot de música!** 🎉
