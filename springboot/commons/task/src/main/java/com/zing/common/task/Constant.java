package com.zing.common.task;

public interface Constant {

    String TASK_PREFIX = "delay.task";

    String TASK_ENABLED = TASK_PREFIX + ".enabled";

    String TASK_REDIS_PREFIX = TASK_PREFIX + ".redis";

    String TASK_REDIS_ENABLED = TASK_REDIS_PREFIX + ".enabled";

    String TASK_REDISSON_PREFIX = TASK_PREFIX + ".redisson";

    String TASK_REDISSON_ENABLED = TASK_REDISSON_PREFIX + ".enabled";

    String TASK_ZOOKEEPER_PREFIX = TASK_PREFIX + ".zookeeper";

    String TASK_ZOOKEEPER_ENABLED = TASK_ZOOKEEPER_PREFIX + ".enabled";

}
