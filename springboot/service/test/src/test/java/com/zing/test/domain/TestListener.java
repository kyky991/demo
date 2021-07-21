package com.zing.test.domain;

import org.springframework.context.ApplicationListener;

public class TestListener implements ApplicationListener<TestEvent> {

    @Override
    public void onApplicationEvent(TestEvent event) {
        event.echo();
    }

}
