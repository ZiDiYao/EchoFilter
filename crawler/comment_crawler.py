import os, requests, json
from dotenv import load_dotenv

# read .env
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
    # 1. Access Token
    token = get_access_token()

    headers = {
        "Authorization": f"bearer {token}",
        "User-Agent": USER_AGENT # User-Agent
    }

    # 3. 构造 URL 并发送 GET 请求
    url = f"https://oauth.reddit.com/comments/{post_id}?limit=100"
    res = requests.get(url, headers=headers)
    #check status code
    res.raise_for_status()
    data = res.json()

    return data

def main():
    print("Reddit API Comments Testing")
    test_post_id = input("please input a reddit postID").strip()

    if not test_post_id:
        print("can not be null")
        return

    try:
        print("start fetching comments from reddit")
        raw_data = fetch_comments(test_post_id)

        comments_list = raw_data[1]["data"]["children"]
        comment_count = sum(1 for item in comments_list if item['kind'] == 't1')
        print(f"successfully get {len(comments_list)} comment_count {comment_count} 。")

        print("\n--- COMMENT DETAILS (Printed Directly) ---")
        comment_number = 1

        # 使用正确的变量名: comments_list
        for item in comments_list:
            # 't1' is the Reddit kind code for a Comment
            if item["kind"] == "t1":
                comment_data = item["data"]

                # Use .get() for optional keys (like 'author') to ensure robustness
                author = comment_data.get("author", "[DELETED]")
                score = comment_data["score"]
                body = comment_data["body"]

                # Print the extracted comment details
                print("-" * 30)
                print(f"[{comment_number}] Author: {author} (Score: {score})")
                print(f"Content:\n{body[:150]}...") # Print first 150 chars of the body

                comment_number += 1

    except requests.exceptions.RequestException as e:
        print("HTTP ERROR")
        print(f"code: {e.response.status_code}")
    except KeyError:
        print("JSON ERROR: Key not found. Check API structure.") # Added descriptive error
    except Exception as e:
        print(f"UNKNOWN ERROR: {e}")


if __name__ == "__main__":
    main()