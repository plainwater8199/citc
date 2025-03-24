package com.citc.nce.aim.privatenumber.controller;

import com.citc.nce.aim.privatenumber.PrivateNumberCallBackApi;
import com.citc.nce.aim.privatenumber.service.PrivateNumberBackService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
public class PrivateNumberCallBackController implements PrivateNumberCallBackApi {

    @Resource
    private PrivateNumberBackService privateNumberBackService;


    @Override
    public PrivateNumberCallBackResp queryProjectInfo(String appKey) {
        return privateNumberBackService.queryProjectInfo(appKey);
    }

    @Override
    public PrivateNumberCallBackResp smsCallBack(List<PrivateNumberCallBack> req) {
        return privateNumberBackService.smsCallBack(req);
    }
}
