"""
Roda UMA VEZ (na VPS) pra criar o perfil de navegador persistente que o
cookie_refresh.py vai usar depois pra manter os cookies sempre frescos.

Importa um cookies.txt já exportado manualmente (extensão "Get cookies.txt
LOCALLY", com uma conta secundária/descartável - veja o README) pra dentro
de um perfil do Playwright, sem nunca passar pela tela de login do Google
(que bloqueia navegadores automatizados). A partir daqui o cookie_refresh.py
mantém essa sessão viva sozinho, sem precisar repetir esse processo.

Uso: python cookie_seed_profile.py
"""

import os
import sys

from cookie_utils import parse_netscape_cookies
from playwright.sync_api import sync_playwright

BASE_DIR = os.path.dirname(__file__)
PROFILE_DIR = os.path.join(BASE_DIR, "browser-profile")
COOKIES_FILE = os.path.join(BASE_DIR, "cookies.txt")


def main():
    if not os.path.exists(COOKIES_FILE):
        print(f"cookies.txt não encontrado em {COOKIES_FILE}.")
        print("Exporte um primeiro (veja o README) antes de rodar este script.")
        sys.exit(1)

    cookies = parse_netscape_cookies(COOKIES_FILE)
    if not cookies:
        print("cookies.txt está vazio ou em formato inválido.")
        sys.exit(1)

    print(f"Importando {len(cookies)} cookies pro perfil em {PROFILE_DIR}...")

    with sync_playwright() as p:
        context = p.chromium.launch_persistent_context(PROFILE_DIR, headless=True)
        context.add_cookies(cookies)
        page = context.new_page()
        page.goto("https://www.youtube.com", wait_until="domcontentloaded")
        page.wait_for_timeout(3000)
        context.close()

    print("Perfil criado com sucesso. A partir de agora, rode cookie_refresh.py")
    print("periodicamente (veja o README para o timer do systemd).")


if __name__ == "__main__":
    main()
