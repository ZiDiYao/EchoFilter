import os, requests
from dotenv import load_dotenv

load_dotenv()
CLIENT_ID = os.getenv("REDDIT_CLIENT_ID")
CLIENT_SECRET = os.getenv("REDDIT_CLIENT_SECRET")
REFRESH_TOKEN = os.getenv("REDDIT_REFRESH_TOKEN")
USER_AGENT = os.getenv("USER_AGENT", "EchoFilterCrawler/0.1")

def get_access_token():
    r = requests.post(
        "https://www.reddit.com/api/v1/access_token",
        auth=requests.auth.HTTPBasicAuth(CLIENT_ID, CLIENT_SECRET),
        data={"grant_type":"refresh_token","refresh_token":REFRESH_TOKEN},
        headers={"User-Agent": USER_AGENT}
    )
    print(r.status_code, r.text)  # 若失败便于排错
    r.raise_for_status()
    return r.json()["access_token"]

if __name__ == "__main__":
    tok = get_access_token()
    me = requests.get(
        "https://oauth.reddit.com/api/v1/me",
        headers={"Authorization": f"bearer {tok}", "User-Agent": USER_AGENT}
    ).json()
    print("OK:", me["name"])
