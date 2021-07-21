package com.zing.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_user")
public class User {

    @TableId
    private Long id;
    private String name;
    private Integer gender;

    @TableLogic
    @TableField(select = false)
    private Boolean delFlag;
}
