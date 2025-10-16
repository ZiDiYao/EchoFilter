from typing import Any, Callable, Optional
from functools import wraps

from typing import Any, Callable, Dict, Optional, List
from functools import wraps

from typing import Any, Callable, Dict, List
from functools import wraps

def extract_post(func: Callable[[Dict[str, Any]], Any]) -> Callable[[List[Any]], Any]:
    """
    Decorator:
    """
    @wraps(func)
    def wrapper(data: List[Any]):
        try:
            post_data = data[0]["data"]["children"][0]["data"]

            # Reddit 提供 permalink（相对路径），组合成完整 URL
            permalink = post_data.get("permalink", "")
            post_url = f"https://www.reddit.com{permalink}" if permalink else ""

            post = {
                "postId": post_data.get("id", ""),
                "title": post_data.get("title", ""),
                "author": post_data.get("author", ""),
                "selftext": post_data.get("selftext", ""),
                "postUrl": post_url,
            }

        except (IndexError, KeyError, TypeError) as e:
            print(f"[extract_post] Error parsing post data: {e}")
            post = {
                "postId": "",
                "title": "",
                "author": "",
                "selftext": "",
                "postUrl": "",
            }

        return func(post)

    return wrapper

@extract_post
def getTitle(post: dict) -> str:
    return post["title"]

@extract_post
def getSelfText(post: dict) -> str:
    return post["selftext"]

@extract_post
def getAuthor(post: dict) -> str:
    return post["author"]

@extract_post
def getPostId(post: dict) -> str:
    return post["postId"]

@extract_post
def getPostUrl(post: dict) -> str:
    return post["postUrl"]
