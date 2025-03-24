package com.citc.nce.misc.captcha.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsSendReq {
    /**
     * 资产标识
     */
    private String appCode;
    /**
     * 签名
     */
    private String sign;
    /**
     * 业务流水号 由调用方生成，保持唯一
     */
    private String bizSn;

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 应用系统账号
     */
    private String userAccount;
    /**
     * 验证码
     */
    private String authCode;

    /**
     * UrlEncode编码
     * 应用系统回调确认用户有效性的地址，应用系统传了这个地址则用户有效性由该地址确定
     */
    private String callBackUrl;
}
