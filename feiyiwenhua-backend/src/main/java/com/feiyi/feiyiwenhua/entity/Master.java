package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("master")
public class Master {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String gender;
    private Date birthDate;
    private String introduction;
    private String achievements;
    private String image;
}