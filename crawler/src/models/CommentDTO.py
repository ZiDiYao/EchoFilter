from pydantic import BaseModel, Field
from typing import Literal

class CommentDTO(BaseModel):
    platform: Literal["reddit", "twitter", "youtube", "rednote"]
    postId: str
    postText: str
    commentText: str
    author: str
    timestamp: float
    upvotes: int