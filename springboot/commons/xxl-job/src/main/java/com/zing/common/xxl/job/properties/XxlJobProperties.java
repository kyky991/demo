package com.zing.common.xxl.job.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    private AdminProperties admin = new AdminProperties();

    private ExecutorProperties executor = new ExecutorProperties();

}