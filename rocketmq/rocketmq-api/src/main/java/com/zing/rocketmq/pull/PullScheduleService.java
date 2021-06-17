package com.zing.rocketmq.pull;

import com.zing.rocketmq.Constant;
import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class PullScheduleService {

    public static void main(String[] args) throws Exception {
        MQPullConsumerScheduleService service = new MQPullConsumerScheduleService("test_consumer_pull_group");
        service.getDefaultMQPullConsumer().setNamesrvAddr(Constant.NAMESRV_ADDR);
        service.setMessageModel(MessageModel.CLUSTERING);
        service.registerPullTaskCallback("test_topic_pull", new PullTaskCallback() {
            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();

                try {

                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0) {
                        offset = 0;
                    }

                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> list = pullResult.getMsgFoundList();
                            for (MessageExt ext : list) {
                                System.out.println(ext);
                            }
                            break;
                        case NO_MATCHED_MSG:
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                        default:
                            break;
                    }
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());
                    context.setPullNextDelayTimeMillis(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        service.start();
    }

}
