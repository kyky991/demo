package com.zing.test.redis.controller;

import com.zing.common.task.DelayHandler;
import com.zing.test.redis.task.dto.BarTask;
import com.zing.test.redis.task.dto.FooTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TaskController {

    @Autowired
    private DelayHandler delayHandler;

    @GetMapping("/task/add")
    public Object add() {

        delayHandler.offer(new FooTask(1L, "233"), 30, TimeUnit.SECONDS);
        delayHandler.offer(new BarTask(1L, "233"), 30, TimeUnit.SECONDS);

        return "11";
    }

    @GetMapping("/task/update")
    public Object update() {
        boolean ret = delayHandler.offer(new FooTask(1L, "233"), 30, TimeUnit.SECONDS);
        return ret;
    }

    @GetMapping("/task/remove")
    public Object remove() {
        return delayHandler.remove(new FooTask(1L, "233"));
    }

    @GetMapping("/task/removeIf")
    public Object removeIf() {
        return delayHandler.removeIf(o -> {
            if (o instanceof FooTask) {
                FooTask t = (FooTask) o;
                return t.getId() == 1;
            }
            return false;
        });
    }

}
