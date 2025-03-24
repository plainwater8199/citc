package com.citc.nce.utils;


import com.citc.nce.authcenter.userDataSyn.UserDataSynApi;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynReq;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class UserDataSynUtilController {
    @Resource
    private UserDataSynApi userDataSynApi;

    @ApiOperation("多租户数据同步")
    @PostMapping("/captcha/tenant/dataSyn")
    public Object dataSyn() {
        return userDataSynApi.dataSyn();
    }

    @ApiOperation("多租户数据统计基础数据同步")
    @PostMapping("/captcha/tenant/statisticBaseDataSyn")
    public Object statisticBaseDataSyn(){
        return userDataSynApi.statisticBaseDataSyn();
    }

    @ApiOperation("多租户数据统计基础数据同步")
    @PostMapping("/captcha/tenant/statisticDataSyn")
    public Object statisticDataSyn() {
         return userDataSynApi.statisticDataSyn();
    }


}
