import requests
import os, requests, json
from dotenv import load_dotenv
from ..models.CommentDTO import CommentDTO
from pathlib import Path
from typing import List

class RedditClient:

    ENV_PATH = Path(__file__).resolve().parents[1] / ".env"
    load_dotenv(ENV_PATH)
    CLIENT_ID = os.getenv("REDDIT_CLIENT_ID")
    CLIENT_SECRET = os.getenv("REDDIT_CLIENT_SECRET")
    REFRESH_TOKEN = os.getenv("REDDIT_REFRESH_TOKEN")
    USER_AGENT = os.getenv("USER_AGENT", "EchoFilterCrawler/0.1")
    BASE_URL = "https://www.reddit.com"

    # {
    #     "kind": "t1",
    #     "data": {
    #         "id": "h1a2b3",
    #         "body": "This is a comment",
    #         "author": "someUser",
    #         "score": 152,
    #         "created_utc": 1730000123,
    #         "replies": {
    #             "data": {
    #                 "children": [...]
    #             }
    #         }
    #     }
    # }

    def fetch_comments(self, subreddit:str, article_id) -> list[CommentDTO]:
        "Base on the URL provided, fetching all comments"
        # token = self.get_access_token()
        url = f"{self.BASE_URL}/r/{subreddit}/comments/{article_id}.json?limit=100&sort=top"
        headers = {
                   "User-Agent": self.USER_AGENT} #"Authorization": f"bearer {token}"
        try:
            resp = requests.get(url, headers = headers)
            resp.raise_for_status()
            # convert the json returned by API into directory
            data = resp.json()
            print(json.dumps(data[:1], indent=2))
            return data

        except Exception as e:
            print("damn", e)
            return None

    def get_access_token(self) -> str:
        "Everytime will ask for a new token, it should be upgraded in the future "
        r = requests.post(
            "https://www.reddit.com/api/v1/access_token",
            auth=requests.auth.HTTPBasicAuth(self.CLIENT_ID, self.CLIENT_SECRET),
            data={"grant_type": "refresh_token", "refresh_token": self.REFRESH_TOKEN},
            headers={"User-Agent": self.USER_AGENT}
        )
        r.raise_for_status()
        return r.json()["access_token"]


