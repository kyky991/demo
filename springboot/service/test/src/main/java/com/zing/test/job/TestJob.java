package com.zing.test.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zing.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestJob {

    @Autowired
    private TestService testService;

    @XxlJob("demoJobHandler")
    public ReturnT<String> demoJobHandler(String param) throws Exception {
        log.info("xxl-job 正在执行，参数：{}", param);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.SUCCESS;
    }

    @XxlJob("testHandler")
    public ReturnT<String> testHandler(String param) throws Exception {
        int shardIndex = XxlJobHelper.getShardIndex();
        log.info("xxl-job 正在执行 testHandler，参数：{}", param);
        if (shardIndex == 0) {
            testService.asyncTest();
            log.info("xxl-job 正在执行 testHandler {}，参数：{}", shardIndex, param);
        } else if (shardIndex == 1) {
            while (true) {
                int check = testService.asyncTestCheck();
                if (check == 0) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                log.info("xxl-job 正在执行 testHandler check {}，参数：{}", check, param);
            }
            log.info("xxl-job 正在执行 testHandler {}，参数：{}", shardIndex, param);
        }
        log.info("xxl-job 正在执行 testHandler - 结束，参数：{}", param);
        return ReturnT.SUCCESS;
    }

}
