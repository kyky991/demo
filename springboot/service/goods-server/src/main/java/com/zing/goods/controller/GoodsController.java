package com.zing.goods.controller;

import com.zing.goods.domain.dto.GoodsDTO;
import com.zing.goods.props.GoodsProperties;
import com.zing.goods.rpc.IGoodsRpc;
import com.zing.user.domain.dto.UserDTO;
import com.zing.user.rpc.IUserRpc;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private HttpServletRequest request;

    @Reference
    private IUserRpc userRpc;

    @Reference(methods = {
            @Method(name = "get", timeout = 2000),
    })
    private IGoodsRpc goodsRpc;

    @Autowired
    private GoodsProperties goodsProperties;

    @GetMapping("/user/{ids}")
    public List<UserDTO> user(@PathVariable Long[] ids) {
        return userRpc.list(Arrays.asList(ids));
    }

    @GetMapping("/goods/{id}")
    public GoodsDTO goods(@PathVariable Long id) {
        return goodsRpc.get(id);
    }

    @GetMapping("/goods/list/{ids}")
    public List<GoodsDTO> goods(@PathVariable Long[] ids) {
        return goodsRpc.list(Arrays.asList(ids));
    }

    @GetMapping("/goods")
    public Integer save() {
        return goodsRpc.save();
    }

    @GetMapping("/test")
    public Integer test(HttpServletRequest request) {
        HttpServletRequest request1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletRequest request2 = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        System.out.println(request);
        System.out.println(this.request);
        System.out.println(request1);
        System.out.println(request2);

        System.out.println(request.equals(this.request));
        System.out.println(request.equals(request1));
        System.out.println(request.equals(request2));

        return RandomUtils.nextInt(0, 100);
    }

    @GetMapping("/prop")
    public GoodsProperties prop() {
        return goodsProperties;
    }

}
