package com.echofilter.lowerLevel.infrastructure.modules.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private String id;                // ULID/UUIDv7/UUID（建议统一 String）
    private String postId;            // 所属帖子
    private String authorAccountId;   // 发评账号（平台账号主体）
    private String content;
    private String analysisResultId;
    private String contentHash;       // 规范化后 SHA-256（可选）
    private String parentCommentId;   // 父评论ID（顶层为 null）
    private String rootId;            // 顶层楼的ID（顶层=自身id）
    private Integer depth;            // 楼层深度（顶层=0）
    // 可选：物化路径，做大规模子树/排序会更快：如 "/<rootId>/<...>/<parentId>/<id>"
    private String path;              // 可为空，后续再启用
    private LocalDateTime createdAt = LocalDateTime.now();
}
