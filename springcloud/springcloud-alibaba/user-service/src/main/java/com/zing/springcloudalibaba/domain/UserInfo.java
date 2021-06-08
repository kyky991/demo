package com.zing.springcloudalibaba.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Data
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    private Integer points;
}
