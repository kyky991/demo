package com.zing.user.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@EnableScheduling
public class UserTask {

    @Scheduled(cron = "0/1 * * * * ?")
    public void echo() {
      log.info("random echo {}", RandomStringUtils.randomAlphabetic(20));
    }

}
