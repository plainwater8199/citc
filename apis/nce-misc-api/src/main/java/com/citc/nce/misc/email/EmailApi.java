package com.citc.nce.misc.email;

import com.citc.nce.misc.email.req.EmailSendReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@FeignClient(value = "misc-service", contextId = "emailApi", url = "${miscServer:}")
public interface EmailApi {

    /**
     * 发送邮件
     *
     * @param emailSendReq
     */
    @PostMapping("/email/sendSimpleMail")
    void sendSimpleMail(@RequestBody @Valid EmailSendReq emailSendReq);

}
