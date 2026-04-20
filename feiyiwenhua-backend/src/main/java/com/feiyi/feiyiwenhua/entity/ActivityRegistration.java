package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long activityId;
    private Long userId;
    private String participantName;
    private String phone;
    private String message;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
