package com.zing.springcloudalibaba.rocketmq;

import com.alibaba.fastjson.JSON;
import com.zing.springcloudalibaba.constant.RocketMQConstant;
import com.zing.springcloudalibaba.domain.Blog;
import com.zing.springcloudalibaba.domain.TransactionLog;
import com.zing.springcloudalibaba.mapper.TransactionLogMapper;
import com.zing.springcloudalibaba.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@RocketMQTransactionListener(txProducerGroup = RocketMQConstant.BLOG_TX_PRODUCER_GROUP)
public class BlogTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        String txId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        // 接收到userId为String类型???
        Long userId = Long.valueOf((String) headers.get("userId"));

        Blog blog = (Blog) arg;

        // 使用stream，如下获取blog
        String json = (String) headers.get("blog");
        if (blog == null && json != null) {
            log.info("blog = {}", json);

            blog = JSON.parseObject(json, Blog.class);
        }

        log.info("txId = {}, userId = {}", txId, userId);

        try {
            blogService.saveBlog(blog, txId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String txId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        TransactionLog log = transactionLogMapper.selectOne(
                TransactionLog.builder().transactionId(txId).build()
        );
        if (log != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
