package com.citc.nce.email;

import com.citc.nce.misc.email.EmailApi;
import com.citc.nce.misc.email.req.EmailSendReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author: ylzouf
 * @Date: 2022/6/29 16:27
 * @Version 1.0
 * @Description:
 */
@Api(tags = "后台管理-邮件服务")
@RestController
public class EmailController {
    @Resource
    private EmailApi emailApi;

    @ApiOperation("发送邮件")
    @PostMapping("/email/sendSimpleMail")
    public void userDetail(@RequestBody EmailSendReq emailSendReq) {
        emailApi.sendSimpleMail(emailSendReq);
    }
}
