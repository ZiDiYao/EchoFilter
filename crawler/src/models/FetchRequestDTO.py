from pydantic import BaseModel, HttpUrl, Field
from typing import Literal

class FetchRequestDTO(BaseModel):
    platform : Literal["reddit", "twitter", "youtube", "rednote"]
    url: HttpUrl
    timestamp: float = Field(
        ..., gt=0, description="Unix timestamp, must be >0"
    )

    class Config:
        schema_extra = {
            "example":{
                    "platform": "reddit",
                    "url": "https://www.reddit.com/r/AskReddit/comments/abc123/",
                    "timestamp": 1730000000
            }
        }
