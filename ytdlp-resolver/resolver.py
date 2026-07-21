"""
Microsserviço local de fallback para o NiiGrando-Song.

Usa yt-dlp para extrair a URL de áudio direta (CDN do YouTube) de vídeos que
o youtube-source (Java) não consegue carregar (ex: restrição de idade/login).
A URL assinada do CDN do Google só aceita requisições com os headers exatos
que o yt-dlp usou pra extrair (senão dá 403), e o LavaPlayer não permite a
gente controlar os headers que ele manda ao seguir um redirect - por isso
esse serviço faz o proxy da requisição de verdade (streaming, sem baixar
tudo antes) em vez de simplesmente redirecionar. Só deve ser acessado pelo
próprio bot, na mesma máquina - não expõe para a internet.

Uso: GET /resolve?url=<url do youtube>
Transmite o áudio em streaming como resposta HTTP.
"""

import os

import requests
import yt_dlp
from flask import Flask, Response, jsonify, request

app = Flask(__name__)

COOKIES_FILE = os.environ.get("YTDLP_COOKIES_FILE", os.path.join(os.path.dirname(__file__), "cookies.txt"))
LISTEN_HOST = os.environ.get("YTDLP_HOST", "127.0.0.1")
LISTEN_PORT = int(os.environ.get("YTDLP_PORT", "8787"))

FORWARDED_RESPONSE_HEADERS = ("Content-Type", "Content-Length", "Content-Range", "Accept-Ranges")


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
    upstream_headers = info.get("http_headers") or {}
    if not stream_url:
        requested_formats = info.get("requested_formats") or []
        if requested_formats:
            stream_url = requested_formats[0].get("url")
            upstream_headers = requested_formats[0].get("http_headers") or upstream_headers

    if not stream_url:
        return jsonify({"error": "yt-dlp não retornou uma URL de stream"}), 502

    headers = dict(upstream_headers)
    range_header = request.headers.get("Range")
    if range_header:
        headers["Range"] = range_header

    try:
        upstream = requests.get(stream_url, headers=headers, stream=True, timeout=30)
    except requests.RequestException as exc:
        return jsonify({"error": f"falha ao conectar no CDN: {exc}"}), 502

    def generate():
        try:
            for chunk in upstream.iter_content(chunk_size=64 * 1024):
                if chunk:
                    yield chunk
        finally:
            upstream.close()

    response_headers = {h: upstream.headers[h] for h in FORWARDED_RESPONSE_HEADERS if h in upstream.headers}
    return Response(generate(), status=upstream.status_code, headers=response_headers)


@app.route("/health")
def health():
    return jsonify({"status": "ok", "cookies_configured": os.path.exists(COOKIES_FILE)})


if __name__ == "__main__":
    app.run(host=LISTEN_HOST, port=LISTEN_PORT)
