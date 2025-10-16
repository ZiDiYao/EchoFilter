from pydantic import BaseModel, Field
from typing import Literal

from pydantic import BaseModel
from typing import Optional

class PostDTO(BaseModel):
    title: str
    author: str
    postText: str
    postId: str
    postUrl: Optional[str] = None
