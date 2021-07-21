package com.zing.test.task.handler;

import com.zing.common.task.TaskHandler;
import com.zing.test.task.dto.BarTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BarTaskHandler implements TaskHandler<BarTask> {

    @Override
    public boolean handle(BarTask task) {
        return false;
    }

}
