package com.zing.mvc.web.controller;

import com.zing.mvc.common.utils.JSONResult;
import com.zing.mvc.utils.ZKCurator;
import com.zing.mvc.web.service.IBuyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Description: 订购商品controller
 */
@Controller
public class BuyController {

    @Autowired
    private IBuyService buyService;

    @Autowired
    private ZKCurator zkCurator;

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/buy")
    @ResponseBody
    public JSONResult doBuy(String itemId) {

        if (StringUtils.isNotBlank(itemId)) {
            buyService.doBuyItem(itemId);
        } else {
            return JSONResult.errorMsg("商品id不能为空");
        }

        return JSONResult.ok();
    }

    @RequestMapping("/isZKAlive")
    @ResponseBody
    public JSONResult isZKAlive() {
        return JSONResult.ok(zkCurator.isZKAlive() ? "连接" : "断开");
    }

}
