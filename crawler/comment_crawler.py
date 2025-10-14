import os, requests, json
from dotenv import load_dotenv

load_dotenv()

CLIENT_ID = os.getenv("REDDIT_CLIENT_ID")
CLIENT_SECRET = os.getenv("REDDIT_CLIENT_SECRET")
REFRESH_TOKEN = os.getenv("REDDIT_REFRESH_TOKEN")
USER_AGENT = os.getenv("USER_AGENT", "EchoFilterCrawler/0.1")

# Step01 Get the access token
# This is because reddit's token can only last for 3600s not like deepseek
def get_access_token():
    r = requests.post(
        "https://www.reddit.com/api/v1/access_token",
        auth=requests.auth.HTTPBasicAuth(CLIENT_ID, CLIENT_SECRET),
        data={"grant_type": "refresh_token", "refresh_token": REFRESH_TOKEN},
        headers={"User-Agent": USER_AGENT}
    )
    r.raise_for_status()
    return r.json()["access_token"]

# Step02 Get comments

def fetch_comments(post_id):

    return -1