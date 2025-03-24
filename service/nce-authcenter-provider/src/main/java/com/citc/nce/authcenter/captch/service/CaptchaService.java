package com.citc.nce.authcenter.captch.service;

import com.citc.nce.authcenter.captcha.vo.req.CaptchaCheckReq;
import com.citc.nce.authcenter.captcha.vo.req.EmailCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.req.SmsCaptchaReq;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaCheckResp;
import com.citc.nce.authcenter.captcha.vo.resp.CaptchaResp;

public interface CaptchaService {


    /**
     * 获取多因子验证码信息
     *
     * @param smsCaptchaReq 请求信息
     * @return 响应结果
     */
    CaptchaResp getDyzSmsCaptcha(SmsCaptchaReq smsCaptchaReq);

    /**
     * 获取邮件信息
     *
     * @param emailCaptchaReq 请求信息
     * @return 响应结果
     */
    CaptchaResp getEmailCaptcha(EmailCaptchaReq emailCaptchaReq);

    /**
     * 验证码校验
     *
     * @param captchaCheckReq 请求信息
     * @return 响应结果
     */
    CaptchaCheckResp checkCaptcha(CaptchaCheckReq captchaCheckReq);

    /**
     * 获取一个错误的验证码
     *
     * @param phone 手机号
     * @return 验证码
     */
    CaptchaResp getErrorCode(String phone);
}
