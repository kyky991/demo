package com.zing.goods.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_goods")
@TableName(value = "t_goods")
public class Goods {

    @Id
    @TableId
    private Long id;

    private String name;

    private String no;

    @TableLogic
    @TableField(select = false)
    private Boolean delFlag;

}
