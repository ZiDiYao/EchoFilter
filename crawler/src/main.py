from .adapters.reddit_client import RedditClient

def main():
    client = RedditClient()
    subreddit = "ontario"
    article_id = "1o58dor"

    print(f"Fetching comments from r/{subreddit}, post {article_id} ...")
    data = client.fetch_comments(subreddit, article_id)

    if data:
        print("\n Successfully fetched data!")
        print(f"Top-level keys: {list(data[0].keys()) if isinstance(data, list) else type(data)}")
    else:
        print(" Failed to fetch comments.")

if __name__ == "__main__":
    main()
