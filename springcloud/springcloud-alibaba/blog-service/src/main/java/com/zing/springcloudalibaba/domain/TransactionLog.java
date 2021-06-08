package com.zing.springcloudalibaba.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rocketmq_transaction_log")
public class TransactionLog {

    @Id
    private Integer id;

    private String transactionId;

    private String log;
}
