package com.citc.nce.misc.email;

import com.citc.nce.misc.email.req.EmailSendReq;
import com.citc.nce.misc.email.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: ylzouf
 * @Date: 2022/6/28 16:38
 * @Version 1.0
 * @Description:
 */
@RestController
@Slf4j
public class EmailController implements EmailApi {

    @Autowired
    private EmailService emailservice;

    @PostMapping("/email/sendSimpleMail")
    @Override
    public void sendSimpleMail(@RequestBody EmailSendReq emailSendReq) {
        emailservice.sendEmail(emailSendReq);
    }
}
