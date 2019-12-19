package com.zing.rabbitmq.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Zing
 * @date 2019-12-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order implements Serializable {

    private Long id;

    private String name;

    private String content;

}
