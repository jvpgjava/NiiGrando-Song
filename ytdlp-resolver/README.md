# yt-dlp resolver (fallback)

Microsserviço Python isolado, opcional. Só entra em ação quando o YouTube
normal (via `youtube-source`, no bot Kotlin) falha com "requires login" —
tipicamente vídeos com restrição de idade. Roda só em `localhost`, nunca
exposto pra internet.

Extrai a URL direta do CDN do YouTube com `yt-dlp` e faz proxy da requisição
em streaming (sem baixar tudo antes) - necessário porque essa URL só aceita
requisições com os headers exatos que o yt-dlp usou pra extraí-la.

## Setup na VPS

```bash
cd /opt/niigrando-song/ytdlp-resolver
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### Cookies (obrigatório pra vídeos restritos funcionarem)

1. No seu PC, instale a extensão **"Get cookies.txt LOCALLY"** no Chrome/Firefox.
2. Loga no YouTube com uma conta **secundária/descartável** (não sua principal —
   automatizar acesso desse jeito foge do uso normal do YouTube e existe risco
   real de a conta levar alguma restrição).
3. Com a extensão, exporta os cookies do `youtube.com` pra um arquivo `cookies.txt`.
4. Copia esse arquivo pra VPS, pra dentro dessa pasta:
   ```bash
   scp cookies.txt usuario@sua-vps:/opt/niigrando-song/ytdlp-resolver/cookies.txt
   ```

Cookies expiram/ficam inválidos com o tempo — se o fallback começar a falhar
depois de um tempo, repete esse processo pra gerar um `cookies.txt` novo.

## Rodar como serviço (systemd)

```bash
sudo tee /etc/systemd/system/niigrando-ytdlp.service > /dev/null <<'EOF'
[Unit]
Description=NiiGrando-Song yt-dlp resolver (fallback local)
After=network-online.target

[Service]
Type=simple
User=SEU_USUARIO
WorkingDirectory=/opt/niigrando-song/ytdlp-resolver
ExecStart=/opt/niigrando-song/ytdlp-resolver/venv/bin/python resolver.py
Restart=on-failure
RestartSec=5
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable --now niigrando-ytdlp
sudo systemctl status niigrando-ytdlp
```

Teste rápido (deve responder `{"status": "ok", ...}`):
```bash
curl http://127.0.0.1:8787/health
```

## Ativar no bot

No `application.yml` do bot Kotlin (`/opt/niigrando-song/application.yml`):
```yaml
ytdlp:
  resolver-url: "http://127.0.0.1:8787"
```

Reinicia o bot (`sudo systemctl restart niigrando-song`) e pronto — vídeos que
falharem por exigir login vão cair automaticamente nesse fallback.

## Atualizar

`yt-dlp` precisa de atualizações frequentes pra acompanhar mudanças do YouTube:
```bash
cd /opt/niigrando-song/ytdlp-resolver
source venv/bin/activate
pip install -U yt-dlp
sudo systemctl restart niigrando-ytdlp
```
