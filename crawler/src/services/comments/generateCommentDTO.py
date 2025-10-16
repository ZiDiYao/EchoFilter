from typing import List
from ...models import CommentDTO


def mapping_post_comments_into_list() -> List[CommentDTO]:
    "Are responsible for generating a list CommentDTO. After this, thos CommentDTO will be converted to event, then send to Kafka"