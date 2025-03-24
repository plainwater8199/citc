package com.citc.nce.authcenter.userDataSyn;

import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynReq;
import com.citc.nce.authcenter.userDataSyn.vo.UserDataSynResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "用户--社区用户同步模块")
@FeignClient(value = "authcenter-service", contextId = "userDataSyn", url = "${authCenter:}")
public interface UserDataSynApi {
    @PostMapping(value = "/captcha/userDataSyn",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    UserDataSynResp userDataSyn(UserDataSynReq req);



    @ApiOperation("多租户数据同步")
    @PostMapping("/captcha/tenant/dataSyn")
    Object dataSyn() ;

//    @ApiOperation("多租户数据统计基础数据同步")
//    @PostMapping("/captcha/tenant/signSyn")
//    Object signSyn();

    @ApiOperation("多租户数据统计基础数据同步")
    @PostMapping("/captcha/tenant/statisticBaseDataSyn")
    Object statisticBaseDataSyn();

    @ApiOperation("多租户数据统计基础数据同步")
    @PostMapping("/captcha/tenant/statisticDataSyn")
    Object statisticDataSyn();


}
