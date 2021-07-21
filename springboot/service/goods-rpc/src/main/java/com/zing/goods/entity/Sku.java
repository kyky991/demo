package com.zing.goods.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_sku")
public class Sku {

    @TableId
    private Long id;

    private Long goodsId;

    private String name;

    private Long price;

    private Integer stock;

    @TableLogic
    @TableField(select = false)
    private Boolean delFlag;

}
