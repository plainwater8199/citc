package com.citc.nce.robot.api;

import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.res.RichMediaSendParamRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "im-service",contextId="RichMediaPlatformApi", url = "${im:}")
public interface RichMediaPlatformApi {

    /**
     * 发送普通消息
     * @param richMediaSendParamRes
     * @return
     */
    @PostMapping("/im/message/richMedia/messageSend")
    RichMediaResultArray messageSend(@RequestBody RichMediaSendParamRes richMediaSendParamRes);

    /**
     * 发送个性消息
     * @param richMediaSendParamRes
     * @return
     */
    @PostMapping("/im/message/richMedia/sendPny")
    RichMediaResultArray sendPny(@RequestBody RichMediaSendParamRes richMediaSendParamRes);
}
