package com.echofilter.commons.enums;

public enum Category {
    INFORMATION,         // 纯信息分享（中立）
    OPINION,             // 个人观点
    MISINFORMATION,      // 明显虚假或误导
    DISINFORMATION,      // 恶意造假（有意图的谣言）
    HATE_SPEECH,         // 仇恨言论
    HARASSMENT,          // 骚扰、辱骂
    TOXICITY,            // 有害或攻击性
    SPAM,                // 广告 / 垃圾信息
    SENSITIVE_CONTENT,   // 敏感内容（暴力、色情等）
    ERROR_INFORMATION,   // 错误/不完整信息（非恶意）
    PROPAGANDA,          // 宣传（政治/商业目的）
    SATIRE,              // 讽刺/反讽（可能引起误解）
    UNSUBSTANTIATED_CLAIM,// 无证据断言
    OUTDATED_INFORMATION,// 过时信息
    OTHER                // 其它（兜底）
}
