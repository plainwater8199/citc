package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimCallBack;
import com.citc.nce.aim.vo.AimCallBackResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>挂短-回调</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:35
 */
@FeignClient(value = "rebot-service",contextId="AimCallBackApi", url = "${robot:}")
public interface AimCallBackApi {

    @GetMapping("/aim/callBack/queryProjectInfo")
    AimCallBackResp queryProjectInfo(@RequestParam("calling") String calling);

    @PostMapping("/aim/callBack/smsCallBack")
    AimCallBackResp smsCallBack(@RequestBody List<AimCallBack> req);
}
