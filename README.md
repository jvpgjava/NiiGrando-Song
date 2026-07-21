# 🎵 NiiGrando-Song

<div align="center">

<img src="assets/NiiGrando.png" alt="NiiGrando-Song" width="220" />

![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**Metade luz, metade sombra — toca qualquer música, de qualquer lugar.**

[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.java.net/)

</div>

---

## 📋 Sobre o Projeto

O **NiiGrando-Song** é um bot de música robusto e confiável para Discord, desenvolvido com as melhores práticas em **Kotlin** e **Spring Boot**. O bot oferece suporte a múltiplas plataformas de áudio, sistema de busca universal e comandos intuitivos.

### 🎯 **Objetivo**
Criar um bot de música moderno, estável e fácil de usar que funcione com plataformas confiáveis, evitando problemas com APIs instáveis.

### ✨ **Características Principais**
- 🎵 **Multi-plataforma** - Suporte a YouTube, SoundCloud, Bandcamp, Twitch, Vimeo e mais
- 🔍 **Busca Universal** - Encontra música em múltiplas plataformas automaticamente
- 🎮 **Comandos Intuitivos** - Interface simples e fácil de usar
- 🛡️ **Robusto** - Sistema de fallback entre plataformas
- 🚀 **Performance** - Desenvolvido com Kotlin e Spring Boot
- 📊 **Logs Detalhados** - Monitoramento completo das atividades

---

## 🛠️ Tecnologias Utilizadas

### **Backend**
- **[Kotlin](https://kotlinlang.org/)** - Linguagem de programação moderna e concisa
- **[Spring Boot 3.2.0](https://spring.io/projects/spring-boot)** - Framework para aplicações Java/Kotlin
- **[JDA 5.0](https://github.com/DV8FromTheWorld/JDA)** - Java Discord API para integração com Discord
- **[LavaPlayer 2.0](https://github.com/sedmelluq/lavaplayer)** - Biblioteca de reprodução de áudio

### **Ferramentas**
- **[Maven](https://maven.apache.org/)** - Gerenciamento de dependências e build
- **[SLF4J + Logback](https://www.slf4j.org/)** - Sistema de logging
- **[Apache HTTP Client](https://hc.apache.org/)** - Cliente HTTP para requisições

### **Plataformas Suportadas**
- ▶️ **YouTube** - Vídeos, músicas, playlists e busca (via [youtube-source](https://github.com/lavalink-devs/youtube-source))
- 🎵 **SoundCloud** - Plataforma principal de música
- 🎨 **Bandcamp** - Música independente
- 📺 **Twitch** - Streams ao vivo
- 🎬 **Vimeo** - Vídeos com áudio
- 🎧 **Mixcloud** - Mixes e podcasts
- 📻 **Rádios Online** - Icecast/Shoutcast
- 💾 **Arquivos Locais** - MP3, WAV, FLAC, OGG, M4A
- 🌐 **HTTP Streams** - Links diretos de áudio

---

## 🚀 Como Executar

### **Pré-requisitos**
- ☕ **Java 17+** ([Download](https://adoptium.net/))
- 🔧 **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- 🤖 **Token do Bot Discord** ([Como obter](#-como-obter-o-token-do-bot))

### **1. Clone o Repositório**
```bash
git clone https://github.com/seu-usuario/niigrando-song.git
cd niigrando-song
```

### **2. Configure o Token**
```bash
# Copie o arquivo de exemplo
cp src/main/resources/application-example.yml src/main/resources/application.yml

# Edite o arquivo e adicione seu token
nano src/main/resources/application.yml
```

### **3. Execute o Bot**

#### **Opção 1 - Script Automático (Recomendado)**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

#### **Opção 2 - Manual**
```bash
# Compilar e executar
mvn clean spring-boot:run

# Ou compilar JAR e executar
mvn clean package
java -jar target/niigrando-song-1.0.0.jar
```

---

## 🤖 Como Obter o Token do Bot

### **1. Acesse o Discord Developer Portal**
Vá para: https://discord.com/developers/applications

### **2. Crie uma Nova Aplicação**
- Clique em **"New Application"**
- Dê um nome para seu bot (ex: "NiiGrando-Song")
- Clique em **"Create"**

### **3. Crie o Bot**
- No menu lateral, clique em **"Bot"**
- Clique em **"Add Bot"**
- Confirme clicando em **"Yes, do it!"**

### **4. Copie o Token**
- Na seção **"TOKEN"**, clique em **"Copy"**
- ⚠️ **IMPORTANTE**: Guarde esse token em segurança!

### **5. Configure as Permissões**
Na seção **"OAuth2 > URL Generator"**:
- **Scopes**: `bot`, `applications.commands`
- **Bot Permissions**:
  - ✅ Read Messages/View Channels
  - ✅ Send Messages
  - ✅ Embed Links
  - ✅ Connect
  - ✅ Speak
  - ✅ Use Voice Activity

### **6. Convide o Bot**
- Copie a URL gerada
- Cole no navegador e selecione seu servidor

---

## 🎮 Comandos Disponíveis

### 🔍 **Busca Universal**
```
!play <nome da música>       # Busca automática em múltiplas plataformas
!find <nome da música>       # Busca universal explícita
```

### 🎵 **Comandos por Plataforma**
```
!play <URL do YouTube>       # YouTube (vídeo, playlist ou busca via !play <nome>)
!soundcloud <nome ou URL>    # SoundCloud
!bandcamp <nome ou URL>      # Bandcamp
!twitch <URL do stream>      # Streams do Twitch
!vimeo <URL do vídeo>        # Vídeos do Vimeo
!mixcloud <URL do mix>       # Mixes do Mixcloud
!radio <URL da rádio>        # Rádios online
!file <caminho do arquivo>   # Arquivos locais
```

### 🎮 **Controles do Player**
```
!pause                       # Pausa a música atual
!resume                      # Retoma a música pausada
!skip                        # Pula para a próxima música
!stop                        # Para a música e limpa a fila
!queue                       # Mostra a fila de músicas
!np                          # Mostra a música atual tocando
```

### ℹ️ **Outros Comandos**
```
!help                        # Lista todos os comandos disponíveis
```

---

## 📁 Estrutura do Projeto

```
src/main/kotlin/com/niigrando/songbot/
├── NiiGrandoSongApplication.kt         # Classe principal
├── config/
│   └── BotConfiguration.kt             # Configuração do JDA
├── audio/
│   ├── AudioPlayerSendHandler.kt       # Handler de áudio para Discord
│   ├── GuildMusicManager.kt            # Gerenciador de música por servidor
│   └── TrackScheduler.kt               # Agendador de fila de músicas
├── command/
│   ├── Command.kt                      # Interface de comando
│   └── impl/
│       ├── PlayCommand.kt              # Comando play (busca universal)
│       ├── UniversalSearchCommand.kt   # Comando find (busca explícita)
│       ├── SoundCloudCommand.kt        # Comando específico SoundCloud
│       ├── BandcampCommand.kt          # Comando específico Bandcamp
│       ├── TwitchCommand.kt            # Comando específico Twitch
│       ├── VimeoCommand.kt             # Comando específico Vimeo
│       ├── MixcloudCommand.kt          # Comando específico Mixcloud
│       ├── RadioCommand.kt             # Comando para rádios online
│       ├── FileCommand.kt              # Comando para arquivos locais
│       ├── PauseCommand.kt             # Comando pause
│       ├── ResumeCommand.kt            # Comando resume
│       ├── SkipCommand.kt              # Comando skip
│       ├── StopCommand.kt              # Comando stop
│       ├── QueueCommand.kt             # Comando queue
│       ├── NowPlayingCommand.kt        # Comando np
│       └── HelpCommand.kt              # Comando help
├── listeners/
│   └── CommandListener.kt              # Listener de comandos do Discord
└── service/
    └── PlayerManager.kt                # Gerenciador de players de áudio
```

---

## 🔧 Configuração Avançada

### **Variáveis de Ambiente**
```bash
# Definir token via variável de ambiente
export DISCORD_TOKEN=seu_token_aqui

# Executar o bot
mvn spring-boot:run
```

### **Configuração de Logs**
Os logs são configurados no arquivo `src/main/resources/logback.xml`:
- **Console**: Logs em tempo real
- **Arquivo**: Logs salvos em `logs/music-bot.log`
- **Rotação**: Logs diários com retenção de 30 dias

### **Permissões do Bot**
O bot precisa das seguintes permissões no Discord:
- **Read Messages/View Channels** - Para ler comandos
- **Send Messages** - Para responder aos comandos
- **Embed Links** - Para enviar mensagens formatadas
- **Connect** - Para conectar aos canais de voz
- **Speak** - Para reproduzir áudio
- **Use Voice Activity** - Para transmitir áudio

---

## 🧪 Testando o Bot

### **Testes Básicos**
```bash
# 1. Verificar se o bot está online
!help

# 2. Testar busca universal
!play despacito

# 3. Testar comandos específicos
!play despacito luis fonsi   # busca no YouTube
!soundcloud linkin park
!bandcamp imagine dragons

# 4. Testar controles
!queue
!np
!skip
```

### **Testes de URLs Diretos**
```bash
# YouTube
!play https://www.youtube.com/watch?v=dQw4w9WgXcQ
!play https://youtu.be/dQw4w9WgXcQ

# SoundCloud
!play https://soundcloud.com/artist/song

# Bandcamp
!play https://bandcamp.com/album/album-name

# Twitch
!twitch https://www.twitch.tv/username

# Vimeo
!vimeo https://vimeo.com/123456789
```

---

## 🐛 Resolução de Problemas

### **O bot não está tocando música**
1. ✅ Verifique se o bot tem permissão para conectar e falar no canal de voz
2. ✅ Certifique-se de que você está no canal de voz antes de usar comandos
3. ✅ Verifique os logs para erros específicos

### **Erro ao compilar**
1. ✅ Certifique-se de estar usando Java 17+
2. ✅ Execute `mvn clean install` para baixar todas as dependências
3. ✅ Verifique se o Maven está instalado corretamente

### **O bot não responde aos comandos**
1. ✅ Verifique se o token está correto
2. ✅ Verifique se o bot tem permissão para ler mensagens
3. ✅ Verifique se o prefixo está correto (padrão: `!`)

### **Problemas com plataformas específicas**
- **YouTube**: Funciona via [youtube-source](https://github.com/lavalink-devs/youtube-source). Se aparecer erro pedindo login ("Sign in to confirm you're not a bot") em algum vídeo específico, configure `youtube.oauth-refresh-token` no `application.yml` (veja [Configuração do YouTube](#-configuração-do-youtube))
- **SoundCloud**: Funciona perfeitamente
- **Bandcamp**: Funciona bem
- **Twitch**: Use URLs diretos de streams
- **Vimeo**: Use URLs diretos de vídeos
- **Arquivos**: Use caminhos absolutos ou URLs file://

### **🔑 Configuração do YouTube (OAuth2, opcional)**
Na maioria dos casos o YouTube funciona sem configuração extra. Mas o YouTube ocasionalmente exige login para tocar certos vídeos (restrição de idade). Se isso acontecer:

1. Rode o bot e, no primeiro uso que exigir login, os logs vão indicar a URL `https://www.google.com/device` e um código — ou gere o token com antecedência usando a própria lib ([instruções](https://github.com/lavalink-devs/youtube-source#oauth2)).
2. Acesse essa URL em um navegador (recomendado usar uma conta Google secundária, não sua conta principal) e digite o código.
3. Copie o refresh token gerado e cole em `src/main/resources/application.yml`:
   ```yaml
   youtube:
     oauth-refresh-token: "SEU_REFRESH_TOKEN_AQUI"
   ```
4. Reinicie o bot.

> **Nota:** na versão atual do `youtube-source` (1.18.1), o único cliente que declara suporte a OAuth (`TV`) vem desabilitado para carregamento de vídeo no próprio código da lib — configurar o token não tem efeito prático em vídeos com restrição de idade hoje. Ele fica configurado só para quando a lib corrigir isso. Para cobrir esses vídeos agora, use o fallback via `yt-dlp` (veja abaixo).

### **🛟 Fallback via yt-dlp (opcional, para vídeos com restrição)**
Para vídeos que o YouTube bloqueia mesmo com OAuth, existe um microsserviço Python opcional em [`ytdlp-resolver/`](ytdlp-resolver/) que usa `yt-dlp` com cookies de uma conta logada para baixar esses vídeos. Roda isolado, só em `localhost`. Veja [`ytdlp-resolver/README.md`](ytdlp-resolver/README.md) para configurar, e ative apontando `ytdlp.resolver-url` no `application.yml`.

---

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT** - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. 🍴 **Fork** o projeto
2. 🌿 **Crie uma branch** para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. 💾 **Commit** suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. 📤 **Push** para a branch (`git push origin feature/NovaFuncionalidade`)
5. 🔄 **Abra um Pull Request**

---

## 📞 Suporte

Se você encontrar algum problema ou tiver dúvidas:

- 🐛 **Reporte bugs** abrindo uma [issue](https://github.com/seu-usuario/niigrando-song/issues)
- 💡 **Sugira melhorias** abrindo uma [issue](https://github.com/seu-usuario/niigrando-song/issues)
- 📧 **Entre em contato** através das issues do GitHub

---

## 🎉 Agradecimentos

- **[JDA](https://github.com/DV8FromTheWorld/JDA)** - Excelente biblioteca para integração com Discord
- **[LavaPlayer](https://github.com/sedmelluq/lavaplayer)** - Biblioteca robusta para reprodução de áudio
- **[Spring Boot](https://spring.io/projects/spring-boot)** - Framework incrível para desenvolvimento
- **[Kotlin](https://kotlinlang.org/)** - Linguagem moderna e expressiva

---

<div align="center">

**Feito com ❤️ usando Kotlin e Spring Boot**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/seu-usuario)
[![Discord](https://img.shields.io/badge/Discord-7289DA?style=for-the-badge&logo=discord&logoColor=white)](https://discord.gg/seu-servidor)

</div>