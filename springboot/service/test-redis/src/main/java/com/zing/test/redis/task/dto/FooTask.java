package com.zing.test.redis.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FooTask {

    private Long id;

    private String name;

    public FooTask(Long id) {
        this.id = id;
    }

}
