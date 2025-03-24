package com.citc.nce.robot.api;

import com.citc.nce.robot.req.DeliveryStatusReq;
import com.citc.nce.robot.req.FontdoGroupSendResultReq;
import com.citc.nce.robot.req.FontdoMessageStatusReq;
import com.citc.nce.robot.vo.DeliveryMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "im-service", contextId = "deliveryNotice", url = "${im:}")
public interface DeliveryNoticeApi {

    @PostMapping("{chatbotId}/delivery/status")
    void receive(@RequestBody DeliveryStatusReq req, @PathVariable("chatbotId") String chatbotId);

    @PostMapping("{appId}/delivery/supplier/status")
    void supplierStatusReceive(@RequestBody FontdoMessageStatusReq req, @PathVariable("appId") String appid);

    @PostMapping("{appId}/delivery/supplier/messageResult")
    void supplierMessageResult(@RequestBody FontdoGroupSendResultReq req, @PathVariable("appId") String appid);


    @PostMapping("{chatbotId}/delivery/message")
    void deliveryMessage(@RequestBody DeliveryMessage deliveryMessage, @PathVariable("chatbotId") String chatbotId);
}
