package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("activity")
public class Activity {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String summary;
    private String description;
    private String images;
    private String location;
    private String organizer;
    private String contact;
    private String type;
    private Date startTime;
    private Date endTime;
    private Integer capacity;
    private Integer signupCount;
    private Integer status;
    private Long heritageId;
    private Date createTime;
    private Date updateTime;
}
