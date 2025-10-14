import os, requests
from dotenv import load_dotenv

load_dotenv()
auth = requests.auth.HTTPBasicAuth(os.getenv("REDDIT_CLIENT_ID"), os.getenv("REDDIT_CLIENT_SECRET"))
data = {"grant_type": "password", "username": os.getenv("REDDIT_USERNAME"), "password": os.getenv("REDDIT_PASSWORD")}
headers = {"User-Agent": os.getenv("USER_AGENT")}
res = requests.post("https://www.reddit.com/api/v1/access_token", auth=auth, data=data, headers=headers)
print(res.json())
tok = res.json()["access_token"]

me = requests.get("https://oauth.reddit.com/api/v1/me", headers={**headers, "Authorization": f"bearer {tok}"}).json()
print("OK:", me["name"])
