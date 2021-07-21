package com.zing.common.task.props;

import com.zing.common.task.Constant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = Constant.TASK_PREFIX)
public class TaskProperties {

    /**
     * 是否开启延迟任务
     */
    private boolean enabled = true;

    /**
     * redis
     */
    private Redis redis = new Redis();

    /**
     * redisson
     */
    private Redisson redisson = new Redisson();

    /**
     * zookeeper
     */
    private Zookeeper zookeeper = new Zookeeper();

    @Data
    public static class Redis {

        private boolean enabled = true;

        private String key;

    }

    @Data
    public static class Redisson {

        private boolean enabled = false;

        private String key;

    }

    @Data
    public static class Zookeeper {

        private boolean enabled = false;

        private String address;

        private String path;

    }

}
