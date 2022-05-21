package com.zing.test.dubbo.service.impl;

import com.zing.test.dubbo.service.DemoService;

public class DemoServiceImpl implements DemoService {

    @Override
    public String echo(String msg) {
        String ret = "echo: " + msg;
        System.out.println(ret);
        return ret;
    }

}
