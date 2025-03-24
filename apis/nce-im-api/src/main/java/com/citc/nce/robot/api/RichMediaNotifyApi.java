package com.citc.nce.robot.api;


import com.citc.nce.robot.req.RichMediaResult;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.vo.TemplateReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "im-service",contextId="RichMediaNotifyApi", url = "${im:}")
public interface RichMediaNotifyApi {

    /**
     * 发送信息回调接口
     * @param richMediaResultArray 回调信息
     */
    @PostMapping("/richMedia/messageNotify")
    String richMediaMessageNotify(@RequestBody RichMediaResultArray richMediaResultArray);

    /**
     * 模板报备回调接口
     * @param richMediaResult 回调信息
     */
    @PostMapping("/richMedia/templateNotify")
    String richMediaTemplateNotify(@RequestBody RichMediaResult richMediaResult);

    /**
     * 模板报备
     * @param templateReq 模板信息
     * @return 平台模板ID
     */
    @PostMapping("/richMedia/templateReport")
    String templateReport(@RequestBody TemplateReq templateReq);
}
