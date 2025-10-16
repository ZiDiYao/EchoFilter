# models/comment_dto.py
from __future__ import annotations  # 允许前向引用（Py3.7+）
from pydantic import BaseModel, Field
from typing import Literal, Optional, List
import PostDTO

class CommentDTO(BaseModel):
    platform: Literal["reddit", "twitter", "youtube", "rednote"] = "reddit"

    # 帖子上下文
    postInfo: PostDTO

    # 当前评论本体
    commentId: str
    author: Optional[str] = None
    commentText: str
    timestamp: float
    score: Optional[int] = None
    permalink: Optional[str] = None

    # 线程上下文（用于 LLM）
    parentId: Optional[str] = None         # 父节点（t1_xxx 或 t3_xxx）
    rootId: Optional[str] = None           # 顶层根评论 id（无则等于自身）
    depth: int = 0                         # 楼层深度（0=顶层）
    threadPath: List[str] = Field(default_factory=list)  # 祖先链（id 列表，根→当前）
