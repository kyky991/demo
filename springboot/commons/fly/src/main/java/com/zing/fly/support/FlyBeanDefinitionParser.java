package com.zing.fly.support;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public class FlyBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String ID_ATTRIBUTE = "id";
    private static final String NAME_ATTRIBUTE = "name";

    private Class<?> beanClass;

    public FlyBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return beanClass;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String id = element.getAttribute(ID_ATTRIBUTE);
        String name = element.getAttribute(NAME_ATTRIBUTE);

        if (StringUtils.hasText(id)) {
            builder.addPropertyValue(ID_ATTRIBUTE, id);
        }
        if (StringUtils.hasText(name)) {
            builder.addPropertyValue(NAME_ATTRIBUTE, name);
        }
    }

}
