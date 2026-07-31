"""Utilitários compartilhados para converter cookies entre o formato Netscape
(cookies.txt, usado pelo yt-dlp) e o formato de dicionário do Playwright."""


def cookies_to_netscape(cookies):
    lines = ["# Netscape HTTP Cookie File", ""]
    for c in cookies:
        domain = c["domain"]
        flag = "TRUE" if domain.startswith(".") else "FALSE"
        path = c.get("path", "/")
        secure = "TRUE" if c.get("secure") else "FALSE"
        expires = c.get("expires", -1)
        expires = int(expires) if expires and expires > 0 else 0
        name = c["name"]
        value = c["value"]
        lines.append("\t".join([domain, flag, path, secure, str(expires), name, value]))
    return "\n".join(lines) + "\n"


def parse_netscape_cookies(path):
    cookies = []
    with open(path, "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split("\t")
            if len(parts) != 7:
                continue
            domain, _flag, cookie_path, secure, expires, name, value = parts
            cookie = {
                "name": name,
                "value": value,
                "domain": domain,
                "path": cookie_path,
                "secure": secure.upper() == "TRUE",
                "httpOnly": False,
            }
            expires_int = int(expires)
            if expires_int > 0:
                cookie["expires"] = expires_int
            cookies.append(cookie)
    return cookies
