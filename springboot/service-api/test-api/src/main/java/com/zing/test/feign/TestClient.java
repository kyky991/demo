package com.zing.test.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "TestClient", url = "http://127.0.0.1:10005")
public interface TestClient {

    @PostMapping("/feign/poll")
    int poll(@RequestParam("msg") String msg);

}
