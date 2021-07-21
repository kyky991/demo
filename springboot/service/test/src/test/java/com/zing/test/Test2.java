package com.zing.test;

import com.zing.test.domain.TestBean;
import com.zing.test.domain.TestEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test2 {

    public static void main(String[] args) throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean2.xml");

        TestBean testBean = (TestBean) applicationContext.getBean("testBean");
        testBean.test();

        applicationContext.publishEvent(new TestEvent("test", "msgmsgmsgmsgmsg"));
    }

}
