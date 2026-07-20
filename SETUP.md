# 🚀 Guia de Configuração Rápida

## ⚡ Setup em 5 Minutos

### **1. Pré-requisitos**
- ☕ Java 17+ ([Download](https://adoptium.net/))
- 🔧 Maven 3.6+ ([Download](https://maven.apache.org/download.cgi))

### **2. Clone e Configure**
```bash
# Clone o repositório
git clone https://github.com/seu-usuario/niigrando-song.git
cd niigrando-song

# Copie o arquivo de configuração
cp src/main/resources/application-example.yml src/main/resources/application.yml
```

### **3. Configure o Token**
Edite `src/main/resources/application.yml`:
```yaml
discord:
  token: "SEU_TOKEN_DO_BOT_AQUI"  # Cole seu token aqui
  prefix: "!"
```

### **4. Execute**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh

# Ou manualmente
mvn spring-boot:run
```

## 🤖 Como Obter Token do Bot

1. **Acesse:** https://discord.com/developers/applications
2. **Crie** uma nova aplicação
3. **Vá para "Bot"** e clique em "Add Bot"
4. **Copie o token** e cole no application.yml
5. **Configure permissões:**
   - Read Messages/View Channels
   - Send Messages
   - Embed Links
   - Connect
   - Speak
   - Use Voice Activity
6. **Convide o bot** usando a URL gerada

## ✅ Teste Rápido

```
!help                    # Ver se o bot está funcionando
!play despacito          # Testar busca universal
!soundcloud linkin park  # Testar comando específico
!queue                   # Ver fila de músicas
```

## 🔧 Comandos Essenciais

### **Música**
```
!play <nome>             # Busca universal
!find <nome>             # Busca explícita
!soundcloud <nome>       # SoundCloud específico
!bandcamp <nome>         # Bandcamp específico
```

### **Controles**
```
!pause                   # Pausar
!resume                  # Retomar
!skip                    # Pular
!stop                    # Parar
!queue                   # Ver fila
!np                      # Música atual
```

## 🆘 Problemas Comuns

### **Bot não conecta**
- ✅ Verifique se o token está correto
- ✅ Verifique se o bot tem permissões necessárias

### **Não toca música**
- ✅ Certifique-se de estar em um canal de voz
- ✅ Verifique se o bot tem permissão para falar

### **Erro de compilação**
- ✅ Verifique se tem Java 17+
- ✅ Execute `mvn clean install`

## 📞 Precisa de Ajuda?

- 🐛 **Bugs:** Abra uma [issue](https://github.com/seu-usuario/niigrando-song/issues)
- 💡 **Sugestões:** Abra uma [issue](https://github.com/seu-usuario/niigrando-song/issues)
- 📖 **Documentação:** Veja o [README.md](README.md) completo

---

**🎉 Pronto! Seu bot de música está funcionando!**
