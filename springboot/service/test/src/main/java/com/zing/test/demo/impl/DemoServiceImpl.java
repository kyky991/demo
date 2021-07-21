package com.zing.test.demo.impl;

import com.zing.test.demo.DemoService;

public class DemoServiceImpl implements DemoService {

    @Override
    public String echo(String msg) {
        String ret = "echo: " + msg;
        System.out.println(ret);
        return ret;
    }

}
