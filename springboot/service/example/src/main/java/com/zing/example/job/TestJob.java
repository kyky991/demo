package com.zing.example.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zing.test.api.ITestApi;
import com.zing.test.feign.TestClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestJob {

    @Autowired
    private TestClient testClient;

    @DubboReference
    private ITestApi testApi;

    @XxlJob("demoJobHandler")
    public ReturnT<String> demoJobHandler(String param) throws Exception {
        log.info("xxl-job 正在执行，参数：{}", param);

        testClient.poll("feign");
        testApi.poll("dubbo");

        return ReturnT.SUCCESS;
    }

}
