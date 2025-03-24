package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;
import com.citc.nce.common.facadeserver.annotations.SkipToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/9 17:50
 */
@RestController
@Slf4j
@Api(value = "aim", tags = "隐私号项目-回调")
public class PrivateNumberCallBackController {

    @Resource
    private PrivateNumberCallBackApi privateNumberCallBackApi;

    @ApiOperation(value = "隐私号-查询项目有效订单", notes = "隐私号-查询项目有效订单")
    @GetMapping("/privateNumber/callBack/queryProjectInfo")
    @SkipToken
    public PrivateNumberCallBackResp queryProjectInfo(@RequestParam("appKey") String appKey) {
        return privateNumberCallBackApi.queryProjectInfo(appKey);
    }

    @ApiOperation(value = "短信发送回调接口", notes = "短信发送回调接口")
    @PostMapping("/privateNumber/callBack/smsCallBack")
    @SkipToken
    PrivateNumberCallBackResp smsCallBack(@RequestBody List<PrivateNumberCallBack> req) {
        return privateNumberCallBackApi.smsCallBack(req);
    }

}
