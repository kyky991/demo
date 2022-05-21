package com.zing.test.dubbo.service;

public interface IGreetingService {

    String echo(String s);

    Result<String> generic(Pojo pojo);

}
