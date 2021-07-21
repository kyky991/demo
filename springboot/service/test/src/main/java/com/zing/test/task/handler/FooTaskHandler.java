package com.zing.test.task.handler;

import com.zing.common.task.TaskHandler;
import com.zing.test.task.dto.FooTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FooTaskHandler implements TaskHandler<FooTask> {

    @Override
    public boolean handle(FooTask task) {
        return false;
    }

}
