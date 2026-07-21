"""
Microsserviço local de fallback para o NiiGrando-Song.

Usa yt-dlp para baixar o áudio de vídeos que o youtube-source (Java) não
consegue carregar (ex: restrição de idade/login). Só deve ser acessado pelo
próprio bot, na mesma máquina - não expõe para a internet.

Uso: GET /resolve?url=<url do youtube>
Retorna o arquivo de áudio baixado como resposta HTTP.
"""

import os
import shutil
import tempfile

import yt_dlp
from flask import Flask, jsonify, request, send_file

app = Flask(__name__)

COOKIES_FILE = os.environ.get("YTDLP_COOKIES_FILE", os.path.join(os.path.dirname(__file__), "cookies.txt"))
LISTEN_HOST = os.environ.get("YTDLP_HOST", "127.0.0.1")
LISTEN_PORT = int(os.environ.get("YTDLP_PORT", "8787"))


@app.route("/resolve")
def resolve():
    url = request.args.get("url")
    if not url:
        return jsonify({"error": "parâmetro 'url' é obrigatório"}), 400

    tmp_dir = tempfile.mkdtemp(prefix="ytdlp-")
    out_template = os.path.join(tmp_dir, "%(id)s.%(ext)s")

    ydl_opts = {
        "format": "bestaudio/best",
        "outtmpl": out_template,
        "quiet": True,
        "no_warnings": True,
        "noplaylist": True,
        "socket_timeout": 30,
    }
    if os.path.exists(COOKIES_FILE):
        ydl_opts["cookiefile"] = COOKIES_FILE

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url, download=True)
            filename = ydl.prepare_filename(info)
    except Exception as exc:
        shutil.rmtree(tmp_dir, ignore_errors=True)
        return jsonify({"error": str(exc)}), 502

    if not os.path.exists(filename):
        shutil.rmtree(tmp_dir, ignore_errors=True)
        return jsonify({"error": "yt-dlp não gerou o arquivo esperado"}), 502

    response = send_file(filename, mimetype="application/octet-stream", conditional=False)

    @response.call_on_close
    def cleanup():
        shutil.rmtree(tmp_dir, ignore_errors=True)

    return response


@app.route("/health")
def health():
    return jsonify({"status": "ok", "cookies_configured": os.path.exists(COOKIES_FILE)})


if __name__ == "__main__":
    app.run(host=LISTEN_HOST, port=LISTEN_PORT)
