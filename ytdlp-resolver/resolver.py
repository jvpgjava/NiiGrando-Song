"""
Microsserviço local de fallback para o NiiGrando-Song.

Usa yt-dlp para transmitir o áudio de vídeos que o youtube-source (Java) não
consegue carregar (ex: restrição de idade/login). Roda o yt-dlp como
subprocesso, gravando direto no stdout (-o -), e repassa os bytes pro
LavaPlayer conforme chegam - assim os headers da requisição pro CDN do
Google ficam sempre corretos (é o próprio yt-dlp que faz a requisição) e a
resposta HTTP começa antes do download terminar. Só deve ser acessado pelo
próprio bot, na mesma máquina - não expõe para a internet.

Uso: GET /resolve?url=<url do youtube>
Transmite o áudio em streaming como resposta HTTP.
"""

import os
import subprocess

from flask import Flask, Response, jsonify, request

app = Flask(__name__)

BASE_DIR = os.path.dirname(__file__)
COOKIES_FILE = os.environ.get("YTDLP_COOKIES_FILE", os.path.join(BASE_DIR, "cookies.txt"))
YTDLP_BIN = os.environ.get("YTDLP_BIN", os.path.join(BASE_DIR, "venv", "bin", "yt-dlp"))
LISTEN_HOST = os.environ.get("YTDLP_HOST", "127.0.0.1")
LISTEN_PORT = int(os.environ.get("YTDLP_PORT", "8787"))

CHUNK_SIZE = 64 * 1024


@app.route("/resolve")
def resolve():
    url = request.args.get("url")
    if not url:
        return jsonify({"error": "parâmetro 'url' é obrigatório"}), 400

    cmd = [
        YTDLP_BIN,
        "-f", "bestaudio/best",
        "--no-playlist",
        "-o", "-",
        "-q",
        "--no-warnings",
    ]
    if os.path.exists(COOKIES_FILE):
        cmd += ["--cookies", COOKIES_FILE]
    cmd.append(url)

    process = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    # Espera o primeiro pedaço de dado real chegar antes de decidir o status HTTP
    # da resposta - se o yt-dlp falhar, não produz nada no stdout.
    first_chunk = process.stdout.read(CHUNK_SIZE)

    if not first_chunk:
        stderr_output = process.stderr.read().decode("utf-8", errors="replace").strip()
        process.wait()
        return jsonify({"error": stderr_output or "yt-dlp não retornou dados"}), 502

    def generate():
        try:
            yield first_chunk
            while True:
                chunk = process.stdout.read(CHUNK_SIZE)
                if not chunk:
                    break
                yield chunk
        finally:
            process.stdout.close()
            process.stderr.close()
            process.wait()

    return Response(generate(), mimetype="application/octet-stream")


@app.route("/health")
def health():
    return jsonify({"status": "ok", "cookies_configured": os.path.exists(COOKIES_FILE)})


if __name__ == "__main__":
    app.run(host=LISTEN_HOST, port=LISTEN_PORT)
