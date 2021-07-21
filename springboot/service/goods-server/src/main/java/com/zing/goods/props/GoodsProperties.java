package com.zing.goods.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "goods")
public class GoodsProperties {

    private String name;

    private Monitor monitor;

    @Data
    public static class Monitor {

        private String name;

    }


}
