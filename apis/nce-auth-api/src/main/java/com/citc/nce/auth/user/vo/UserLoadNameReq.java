package com.citc.nce.auth.user.vo;

import lombok.Data;

/**
 * 手机号登录
 *
 * @author huangchong
 * @date 2022/7/13 17:40
 * @describe
 */
@Data
public class UserLoadNameReq {
    /**
     * 登录账号：用户名或者手机号
     */
    private String account;

    private String password;
//    /**
//     * 图片验证码
//     */
//    private String img_msg;
    /**
     * 短信验证码
     */
    private String smsCode;
    /**
     * 短信验证key
     */
    private String smsCaptchaKey;
//    /**
//     * 二维码验证key
//     */
//    private String imageCaptchaKey;
}
