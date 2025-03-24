package com.citc.nce.authcenter.captcha;

import com.citc.nce.authcenter.captcha.vo.req.*;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;
import com.citc.nce.authcenter.captcha.vo.resp.ImageCaptchaResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "认证中心--验证码模块")
@FeignClient(value = "authcenter-service", contextId = "captcha", url = "${authCenter:}")
public interface CaptchaApi {
    @ApiOperation("获取短信验证码")
    @PostMapping("/captcha/getSmsCaptcha")
    CaptchaResp getSmsCaptcha(SmsCaptchaReq smsCaptchaReq);
    @ApiOperation("多因子发送验证码")
    @PostMapping("/captcha/getDyzSmsCaptcha")
    CaptchaResp getDyzSmsCaptcha(SmsCaptchaReq smsCaptchaReq);
    @ApiOperation("获取邮箱验证码")
    @PostMapping("/captcha/getEmailCaptcha")
    CaptchaResp getEmailCaptcha(EmailCaptchaReq emailCaptchaReq);
    @ApiOperation("校验验证码")
    @PostMapping("/captcha/checkCaptcha")
    CaptchaCheckResp checkCaptcha(CaptchaCheckReq captchaCheckReq);
    @ApiOperation("获取-本人-多因子发送验证码")
    @PostMapping("/selfCaptcha/getSelfDyzSmsCaptcha")
    CaptchaResp getSelfDyzSmsCaptcha(SmsCaptchaReq smsCaptchaReq);

    @ApiOperation("获取-本人-邮箱验证码")
    @PostMapping("/selfCaptcha/getSelfEmailCaptcha")
    CaptchaResp getSelfEmailCaptcha(SelfEmailCaptchaReq emailCaptchaReq);
}
