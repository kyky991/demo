package com.zing.springcloudalibaba.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zing
 */
@Data
@Entity
@Table(name = "t_order")
public class Order {

    @Id
    private Long id;
    private String name;
    private Long productId;
    private Integer count;
    private Integer points;
    private Date createTime;

}
