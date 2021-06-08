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
 * @date 2020-07-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "points_log")
public class PointsLog {

    @Id
    private Long id;

    private Long userId;

    private Integer points;

    private String event;

    private Date createTime;

}
