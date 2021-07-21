package com.zing.fly.support;

import com.zing.fly.domain.Bar;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class FlyNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("bar", new FlyBeanDefinitionParser(Bar.class));
    }
}
