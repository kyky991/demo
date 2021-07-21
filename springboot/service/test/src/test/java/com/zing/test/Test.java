package com.zing.test;

import com.zing.fly.domain.Bar;
import com.zing.test.domain.*;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class Test {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");

        ClassPathResource resource = new ClassPathResource("bean.xml");
        XmlBeanFactory beanFactory = new XmlBeanFactory(resource);

        TestBean testBean = (TestBean) beanFactory.getBean("testBean");
        testBean.test();

        GetBeanTest getBeanTest = (GetBeanTest) beanFactory.getBean("getBeanTest");
        getBeanTest.show();

        ChangeMethod changeMethod = (ChangeMethod) beanFactory.getBean("changeMethod");
        changeMethod.change();

        Bar bar = (Bar) beanFactory.getBean("testBar");
        System.out.println(bar);

        Car car = (Car) beanFactory.getBean("car");
        CarFactoryBean carFactoryBean = (CarFactoryBean) beanFactory.getBean("&car");

        car = (Car) beanFactory.getBean("car");

    }

}
