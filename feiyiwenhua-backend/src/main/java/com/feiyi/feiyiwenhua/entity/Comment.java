package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("community_comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;
    private Long userId;
    private String userName;
    private String content;
    private Long parentId; // 父评论ID，0表示顶级评论
    private Long replyUserId; // 被回复用户ID（用于 @回复 显示）
    private String replyUserName; // 被回复用户名
    private Integer likeCount;
    private Integer status; // 0: 正常, 1: 删除
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private String userAvatar; // 用户头像URL（非数据库字段）

    @TableField(exist = false)
    private String userInitial; // 用户首字母（非数据库字段）

    @TableField(exist = false)
    private Boolean isLiked; // 用户是否已点赞（非数据库字段）

    // 获取用户首字母（用于头像占位符）
    public String getUserInitial() {
        if (userInitial != null) {
            return userInitial;
        }
        if (userName != null && !userName.trim().isEmpty()) {
            return userName.substring(0, 1).toUpperCase();
        }
        return "?";
    }

    // 检查是否有用户头像
    public boolean hasUserAvatar() {
        return userAvatar != null && !userAvatar.trim().isEmpty();
    }
}