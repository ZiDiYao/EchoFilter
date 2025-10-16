from pydantic import BaseModel, Field
from typing import Literal
import CommentDTO
class CommentIngestedEvent(BaseModel):
    eventId: str
    comment : CommentDTO
    ingestion_timestamp: float

