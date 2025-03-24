package com.citc.nce.authcenter.captcha.vo.req;

import lombok.Builder;
import lombok.Data;

/**
 * @author hhluop
 */
@Data
@Builder
public class SmsSendRequest {
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String authCode;
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
     * 用户身份验证接口，提供给dyz平台回调确认用户身份
     */
    private String callBackUrl;
}
