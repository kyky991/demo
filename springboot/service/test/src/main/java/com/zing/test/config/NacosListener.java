package com.zing.test.config;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NacosListener {

    @NacosConfigListener(dataId = "test.yml", type = ConfigType.YAML)
    public void dataChanged(String content) {
        log.info(content);
    }

}
