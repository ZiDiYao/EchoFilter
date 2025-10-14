# get_refresh_token.py
import os, secrets, webbrowser, urllib.parse, http.server, socketserver, requests
from dotenv import load_dotenv

load_dotenv()
CLIENT_ID = os.getenv("REDDIT_CLIENT_ID")
CLIENT_SECRET = os.getenv("REDDIT_CLIENT_SECRET")
REDIRECT_URI = os.getenv("REDIRECT_URI", "http://localhost:8080")
USER_AGENT = os.getenv("USER_AGENT", "EchoFilterCrawler/0.1")
SCOPES = "read identity"

state = secrets.token_urlsafe(16)
params = {
    "client_id": CLIENT_ID,
    "response_type": "code",
    "state": state,
    "redirect_uri": REDIRECT_URI,
    "duration": "permanent",
    "scope": SCOPES,
}
auth_url = "https://www.reddit.com/api/v1/authorize?" + urllib.parse.urlencode(params)

class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        q = urllib.parse.parse_qs(urllib.parse.urlparse(self.path).query)
        if q.get("state", [""])[0] != state:
            self.send_response(400); self.end_headers(); self.wfile.write(b"State mismatch"); return
        code = q.get("code", [""])[0]
        self.send_response(200); self.end_headers(); self.wfile.write(b"You can close this window.")
        r = requests.post(
            "https://www.reddit.com/api/v1/access_token",
            auth=requests.auth.HTTPBasicAuth(CLIENT_ID, CLIENT_SECRET),
            data={"grant_type":"authorization_code","code":code,"redirect_uri":REDIRECT_URI},
            headers={"User-Agent": USER_AGENT}
        )
        print("Token response:", r.status_code, r.text)
        rt = r.json().get("refresh_token")
        if rt: print("\n REFRESH TOKEN:\n", rt)

PORT = urllib.parse.urlparse(REDIRECT_URI).port or 8080
with socketserver.TCPServer(("127.0.0.1", PORT), Handler) as httpd:
    print("Opening browser:", auth_url)
    webbrowser.open(auth_url)
    httpd.handle_request()
