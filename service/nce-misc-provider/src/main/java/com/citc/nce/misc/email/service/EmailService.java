package com.citc.nce.misc.email.service;


import com.citc.nce.misc.email.req.EmailSendReq;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
public interface EmailService {
    void sendEmail(EmailSendReq emailDetailReq);
}
