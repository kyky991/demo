package com.zing.test.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@Setter
@ToString
public class Task {

    private int type;
    private Object source;

    public Task() {
    }

    public Task(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return type == task.type &&
                Objects.equals(source, task.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source);
    }
}
