package com.zing.rocketmq.pull;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PullConsumer {

    private static final Map<MessageQueue, Long> OFFSET = new HashMap<>();

    public static void main(String[] args) throws Exception {
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("test_consumer_pull_group");
        consumer.setNamesrvAddr(Constant.NAMESRV_ADDR);
        consumer.start();

        Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("test_pull_topic");
        for (MessageQueue mq : mqs) {
            SINGLE_MQ:
            while (true) {
                Long offset = OFFSET.getOrDefault(mq, 0L);

                PullResult pullResult = consumer.pullBlockIfNotFound(mq, null, offset, 32);
                System.out.println(pullResult);

                OFFSET.put(mq, pullResult.getNextBeginOffset());

                switch (pullResult.getPullStatus()) {
                    case FOUND:
                        List<MessageExt> list = pullResult.getMsgFoundList();
                        for (MessageExt ext : list) {
                            System.out.println(ext);
                        }
                        break;
                    case NO_MATCHED_MSG:
                        break;
                    case NO_NEW_MSG:
                        break SINGLE_MQ;
                    case OFFSET_ILLEGAL:
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
