/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citc.nce.auth.example.controller;

import com.citc.nce.auth.example.feign.MyClient;
import com.citc.nce.auth.example.feign.OrderClient;
import com.citc.nce.auth.example.feign.UserClient;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试接口
 *
 * @author freeman
 */
@RestController
public class TestController {

    @Autowired
    private UserClient userClient;
    @Autowired
    private OrderClient orderClient;
    @Resource
    CspCustomerApi customerApi;
    @Resource
    private MyClient myClient;

    @GetMapping("/test/default/{ok}")
    public String testDefault(@PathVariable boolean ok) {
        return orderClient.defaultConfig(ok);
    }


    @GetMapping("/test/timeout/{t}")
    public String testTimeout(@PathVariable("t") Integer t) {
        String timeout = null;
        try {
            timeout = myClient.timeout(t);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeout;
    }

    @GetMapping("/test/feign/{ok}")
    public String testFeign(@PathVariable boolean ok) {
        return userClient.feign(ok);
    }

    @GetMapping("/test/feignMethod/{ok}")
    public String testFeignMethod(@PathVariable boolean ok) {
        return userClient.feignMethod(ok);
    }

    @GetMapping("/csp/customer/getBalance")
    Long getBalance(@RequestParam("customerId") String customerId) {
        return customerApi.getBalance(customerId);
    }

    @GetMapping("/csp/customer/addBalance")
    Long addBalance(@RequestParam("customerId") String customerId, @RequestParam("addMoney") Long addMoney) {
        return customerApi.addBalance(customerId, addMoney);
    }

    /**
     * 扣除余额
     *
     * @param customerId 客户编号
     * @param isTryMax   余额不足，是否尽量扣除
     * @return 扣除余额
     */
    @GetMapping("/csp/customer/reduceBalance")
    ReduceBalanceResp reduceBalance(@RequestParam("customerId") String customerId, @RequestParam("price") Long price, @RequestParam("num") Long num, @RequestParam("isTryMax") boolean isTryMax) {
        return customerApi.reduceBalance(customerId, price, num, isTryMax);
    }

}
