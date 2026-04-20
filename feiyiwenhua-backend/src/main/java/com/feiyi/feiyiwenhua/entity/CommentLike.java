package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("community_comment_like")
public class CommentLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long commentId;
    private Long userId;
    private Date createTime;
}