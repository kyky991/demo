package com.zing.springcloudalibaba.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blog")
public class Blog {

    @Id
    private Long id;

    private Long userId;

    private String summary;

    private String tags;

    private String title;

    private String content;

    private Date createTime;
}
