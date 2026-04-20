package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("community_post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;
    private String images;
    private String coverImage;
    private Long authorId;
    private String authorName;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status; // 0: 草稿, 1: 已发布, 2: 隐藏
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private Boolean likedByCurrentUser;

    @TableField(exist = false)
    private String authorAvatar; // 作者头像URL（非数据库字段）

    @TableField(exist = false)
    private String authorInitial; // 作者首字母占位（非数据库字段）
}
