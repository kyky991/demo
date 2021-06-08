package com.zing.springcloudalibaba.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zing
 */
@Data
@Entity
@Table(name = "t_account")
public class Account {

    @Id
    private Long id;
    private Long userId;
    private Long total;

}
