package com.citc.nce.im.controller;

import com.citc.nce.im.richMedia.RichMediaPlatformService;
import com.citc.nce.robot.api.RichMediaPlatformApi;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.res.RichMediaSendParamRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>视屏短信</p>
 * ping chen
 */
@RestController
@Slf4j
public class RichMediaPlatformController implements RichMediaPlatformApi {

    @Resource
    RichMediaPlatformService richMediaPlatformService;

    /**
     * 发送普通消息
     * @param richMediaSendParamRes
     * @return
     */
    @Override
    public RichMediaResultArray messageSend(RichMediaSendParamRes richMediaSendParamRes) {
        return richMediaPlatformService.messageSend(richMediaSendParamRes);
    }

    /**
     * 发送个性消息
     * @param richMediaSendParamRes
     * @return
     */
    @Override
    public RichMediaResultArray sendPny(RichMediaSendParamRes richMediaSendParamRes) {
        return richMediaPlatformService.sendPny(richMediaSendParamRes);
    }
}
