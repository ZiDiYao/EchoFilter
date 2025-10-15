from pydantic import BaseModel, Field
from typing import Literal

class CommentIngestedEvent(BaseModel):
    eventId: str
