"""
Roda periodicamente (via systemd timer, na VPS) para manter os cookies do
YouTube frescos. Usa o mesmo perfil de navegador criado pelo
cookie_login_setup.py (rodado uma vez no PC, e copiado pra cá).

Uso: python cookie_refresh.py
"""

import os
import sys

from cookie_utils import cookies_to_netscape
from playwright.sync_api import sync_playwright

BASE_DIR = os.path.dirname(__file__)
PROFILE_DIR = os.path.join(BASE_DIR, "browser-profile")
COOKIES_FILE = os.path.join(BASE_DIR, "cookies.txt")


def main():
    if not os.path.isdir(PROFILE_DIR):
        print(f"Perfil não encontrado em {PROFILE_DIR}.")
        print("Rode cookie_login_setup.py no seu PC primeiro e copie a pasta pra cá.")
        sys.exit(1)

    with sync_playwright() as p:
        context = p.chromium.launch_persistent_context(PROFILE_DIR, headless=True)
        page = context.new_page()
        page.goto("https://www.youtube.com", wait_until="domcontentloaded")
        page.wait_for_timeout(3000)

        cookies = context.cookies()
        context.close()

    with open(COOKIES_FILE, "w", encoding="utf-8") as f:
        f.write(cookies_to_netscape(cookies))

    print(f"cookies.txt atualizado com {len(cookies)} cookies.")


if __name__ == "__main__":
    main()
