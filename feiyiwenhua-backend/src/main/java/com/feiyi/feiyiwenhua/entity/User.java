package com.feiyi.feiyiwenhua.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String phone;
    private String password;
    private String avatar;
    private String role;
    private Integer status;
    private String verificationCode;
    private Date codeExpireTime;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private String displayName;

    public String getDisplayName() {
        if (displayName != null && !displayName.trim().isEmpty()) {
            return displayName;
        }
        if (username != null && !username.trim().isEmpty()) {
            return username;
        }
        if (phone != null && !phone.trim().isEmpty()) {
            return phone;
        }
        return "用户";
    }

    public String getInitial() {
        String name = getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.substring(0, 1).toUpperCase();
    }

    public boolean hasAvatar() {
        return avatar != null && !avatar.trim().isEmpty();
    }
}
