package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("heritage")
public class Heritage {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String description;
    private String history;
    private String process;
    private String materials;
    private String images;
    private String videoUrl;
    private Integer level; // 1:国家级, 2:省级, 3:市级
    private Date createTime;
    private Date updateTime;
    private Long categoryId;
    private Long masterId;
}