package com.citc.nce.im.controller;

import com.citc.nce.robot.api.SendDetailsApi;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.HomePageResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendDetailsController implements SendDetailsApi {

    @PostMapping("/im/test")
    public void test(@RequestBody SendPageReq pageReq) {
    }

    @PostMapping("/im/message/queryHomePage")
    @Override
    public HomePageResp queryHomePage(@RequestBody SendPageReq pageReq) {
        return null;//quantityStatisticsService.queryHomePage(pageReq);
    }

    @Override
    @PostMapping("/im/message/queryHomeBrokenLineNum")
    public HomePageResp queryHomeBrokenLineNum(@RequestBody SendPageReq pageReq) {
        return null;//quantityStatisticsService.queryHomeBrokenLineNum(pageReq);
    }

    @PostMapping("/im/message/queryHomeBrokenLineNumByHour")
    @Override
    public HomePageResp queryHomeBrokenLineNumByHour() {
        return null;//quantityStatisticsService.queryHomeBrokenLineNumByHour();
    }

    @PostMapping("/im/message/testtest")
    public void queryHomeBrokenLineNumByHou2r() {
    }
}
