package com.zing.test.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.zing.test.dubbo.service.IGreetingService;
import com.zing.test.dubbo.service.Pojo;
import com.zing.test.dubbo.service.Result;
import org.apache.dubbo.common.bytecode.Wrapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

@DubboService
public class GreetingServiceImpl implements IGreetingService {

    @Override
    public String echo(String s) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s + " " + RpcContext.getContext().getAttachment("company");
    }

    @Override
    public Result<String> generic(Pojo pojo) {
        Result<String> result = new Result<>();
        result.setSuccess(true);
        result.setData(JSON.toJSONString(pojo));
        return result;
    }

    public static void main(String[] args) throws Exception {
        Wrapper wrapper = Wrapper.getWrapper(IGreetingService.class);
        System.out.println(wrapper);

        Thread.sleep(Integer.MAX_VALUE);
    }

}
