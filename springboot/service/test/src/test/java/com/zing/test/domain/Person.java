package com.zing.test.domain;

import lombok.Data;

@Data
public class Person {

    private Integer age;

    public void show() {
        System.out.println("person");
    }

}
