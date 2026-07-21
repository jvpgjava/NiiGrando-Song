"""
Microsserviço local de fallback para o NiiGrando-Song.

Usa yt-dlp para extrair a URL de áudio direta (CDN do YouTube) de vídeos que
o youtube-source (Java) não consegue carregar (ex: restrição de idade/login).
Só faz extração, não baixa nada - o LavaPlayer é redirecionado pra URL direta
e transmite de lá, então a resposta é praticamente instantânea. Só deve ser
acessado pelo próprio bot, na mesma máquina - não expõe para a internet.

Uso: GET /resolve?url=<url do youtube>
Responde com um redirect (302) para a URL direta do stream de áudio.
"""

import os

import yt_dlp
from flask import Flask, jsonify, redirect, request

app = Flask(__name__)

COOKIES_FILE = os.environ.get("YTDLP_COOKIES_FILE", os.path.join(os.path.dirname(__file__), "cookies.txt"))
LISTEN_HOST = os.environ.get("YTDLP_HOST", "127.0.0.1")
LISTEN_PORT = int(os.environ.get("YTDLP_PORT", "8787"))


@app.route("/resolve")
def resolve():
    url = request.args.get("url")
    if not url:
        return jsonify({"error": "parâmetro 'url' é obrigatório"}), 400

    ydl_opts = {
        "format": "bestaudio/best",
        "quiet": True,
        "no_warnings": True,
        "noplaylist": True,
        "socket_timeout": 30,
    }
    if os.path.exists(COOKIES_FILE):
        ydl_opts["cookiefile"] = COOKIES_FILE

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url, download=False)
    except Exception as exc:
        return jsonify({"error": str(exc)}), 502

    stream_url = info.get("url")
    if not stream_url:
        requested_formats = info.get("requested_formats") or []
        if requested_formats:
            stream_url = requested_formats[0].get("url")

    if not stream_url:
        return jsonify({"error": "yt-dlp não retornou uma URL de stream"}), 502

    return redirect(stream_url, code=302)


@app.route("/health")
def health():
    return jsonify({"status": "ok", "cookies_configured": os.path.exists(COOKIES_FILE)})


if __name__ == "__main__":
    app.run(host=LISTEN_HOST, port=LISTEN_PORT)
