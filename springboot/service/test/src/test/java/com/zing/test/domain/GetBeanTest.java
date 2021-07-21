package com.zing.test.domain;

public abstract class GetBeanTest {

    public abstract Person getPerson();

    public void show() {
        getPerson().show();
    }

}
