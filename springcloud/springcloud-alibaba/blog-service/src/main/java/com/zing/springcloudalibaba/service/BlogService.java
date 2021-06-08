package com.zing.springcloudalibaba.service;

import com.alibaba.fastjson.JSON;
import com.zing.springcloudalibaba.constant.RocketMQConstant;
import com.zing.springcloudalibaba.domain.Blog;
import com.zing.springcloudalibaba.domain.TransactionLog;
import com.zing.springcloudalibaba.domain.messaging.BlogPointsDTO;
import com.zing.springcloudalibaba.mapper.BlogMapper;
import com.zing.springcloudalibaba.mapper.TransactionLogMapper;
import com.zing.springcloudalibaba.rocketmq.PointsSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Service
public class BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private PointsSource pointsSource;

    public Blog getById(Long id) {
        return blogMapper.selectByPrimaryKey(id);
    }

    public void postBlog(Blog blog) {
        String txId = UUID.randomUUID().toString();
        TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(
                RocketMQConstant.BLOG_TX_PRODUCER_GROUP,
                RocketMQConstant.POINTS_TOPIC,
                MessageBuilder
                        .withPayload(BlogPointsDTO.builder().userId(blog.getUserId()).points(20).build())
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, txId)
                        .setHeader("userId", blog.getUserId())
                        .build(),
                blog
        );
        log.info(result.toString());
    }

    public void postBlog2(Blog blog) {
        String txId = UUID.randomUUID().toString();

        // 使用stream方式
        pointsSource.output().send(
                MessageBuilder
                        .withPayload(BlogPointsDTO.builder().userId(blog.getUserId()).points(20).build())
                        .setHeader(RocketMQHeaders.TRANSACTION_ID, txId)
                        .setHeader("userId", blog.getUserId())
                        .setHeader("blog", JSON.toJSONString(blog))
                        .build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBlog(Blog blog, String txId) {
        blogMapper.insert(blog);

        transactionLogMapper.insert(
                TransactionLog.builder().transactionId(txId).log("加积分").build()
        );
    }
}
