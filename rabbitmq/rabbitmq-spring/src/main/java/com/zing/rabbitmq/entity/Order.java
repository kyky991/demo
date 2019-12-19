package com.zing.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Zing
 * @date 2019-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    private Long id;

    private String name;

    private String content;

}
