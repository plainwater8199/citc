package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimCallBack;
import com.citc.nce.aim.vo.AimCallBackResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/9 17:50
 */
@RestController
@Slf4j
@Api(value = "aim", tags = "挂短-回调")
public class AimCallBackController {

    @Resource
    private AimCallBackApi AimCallBackApi;

    @ApiOperation(value = "查询项目有效订单", notes = "查询项目有效订单")
    @GetMapping("/aim/callBack/queryProjectInfo")
    public AimCallBackResp queryProjectInfo(@RequestParam("calling") String calling) {
        return AimCallBackApi.queryProjectInfo(calling);
    }

    @ApiOperation(value = "短信发送回调接口", notes = "短信发送回调接口")
    @PostMapping("/aim/callBack/smsCallBack")
    AimCallBackResp smsCallBack(@RequestBody List<AimCallBack> req) {
        return AimCallBackApi.smsCallBack(req);
    }

}
