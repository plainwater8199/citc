package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;
import com.citc.nce.aim.vo.AimCallBack;
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
@FeignClient(value = "rebot-service",contextId="PrivateNumberCallBackApi", url = "${robot:}")
public interface PrivateNumberCallBackApi {

    @GetMapping("/privateNumber/callBack/queryProjectInfo")
    PrivateNumberCallBackResp queryProjectInfo(@RequestParam("appKey") String appKey);

    @PostMapping("/privateNumber/callBack/smsCallBack")
    PrivateNumberCallBackResp smsCallBack(@RequestBody List<PrivateNumberCallBack> req);
}
