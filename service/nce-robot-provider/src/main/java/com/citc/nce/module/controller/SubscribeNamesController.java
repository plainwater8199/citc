package com.citc.nce.module.controller;

import com.citc.nce.module.SubscribeNamesApi;
import com.citc.nce.module.service.SubscribeNamesService;
import com.citc.nce.module.vo.req.GetSubscribeInfoByUserReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.resp.GetSubscribeInfoByUserResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/csp/subscribeNames")
@Api(value = "subscribeNamesController", tags = "CSP--订阅名单")
public class SubscribeNamesController implements SubscribeNamesApi {

    @Resource
    private SubscribeNamesService subscribeNamesService;


    @PostMapping("/save")
    @ApiOperation(value = "订阅名单新增保存", notes = "订阅名单新增保存")
    public int saveSubscribeNames(SubscribeNamesReq req) {
        return subscribeNamesService.saveSubscribeNames(req);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "订阅取消", notes = "订阅取消")
    public int cancelSubscribeNames(SubscribeNamesReq req) {
        return subscribeNamesService.cancelSubscribeNames(req);
    }

    @PostMapping("/update")
    @ApiOperation(value = "订阅名单更新", notes = "订阅名单更新")
    public int updateSubscribeNames(SubscribeNamesReq req) {
        return subscribeNamesService.updateSubscribeNames(req);
    }

    @PostMapping("/getSubscribeNamesList")
    @ApiOperation(value = "订阅名单列表查询", notes = "订阅名单列表查询")
    public PageResult<SubscribeNamesReq> getSubscribeNamesList(SubscribeNamesReq req) {
        return subscribeNamesService.getSubscribeNamesList(req);
    }

    @PostMapping("/getSubscribeInfoByPhone")
    @ApiOperation(value = "获取指定用户的订阅发送详情", notes = "获取指定用户的订阅发送详情")
    public GetSubscribeInfoByUserResp getSubscribeInfoByPhone(GetSubscribeInfoByUserReq req) {
        return subscribeNamesService.getSubscribeInfoByPhone(req);
    }



}
