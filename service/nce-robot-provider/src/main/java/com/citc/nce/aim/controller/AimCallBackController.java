package com.citc.nce.aim.controller;

import com.citc.nce.aim.AimCallBackApi;
import com.citc.nce.aim.service.AimCallBackService;
import com.citc.nce.aim.vo.AimCallBack;
import com.citc.nce.aim.vo.AimCallBackResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>挂短-订单</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:59
 */
@RestController
public class AimCallBackController implements AimCallBackApi {

    @Resource
    AimCallBackService service;


    @Override
    public AimCallBackResp queryProjectInfo(String calling) {
        return service.queryProjectInfo(calling);
    }

    @Override
    public AimCallBackResp smsCallBack(List<AimCallBack> req) {
        return service.smsCallBack(req);
    }
}
