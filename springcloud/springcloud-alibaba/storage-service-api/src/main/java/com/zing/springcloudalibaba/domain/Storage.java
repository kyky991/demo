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
@Table(name = "t_storage")
public class Storage {

    @Id
    private Long id;
    private String name;
    private Integer stock;

}
