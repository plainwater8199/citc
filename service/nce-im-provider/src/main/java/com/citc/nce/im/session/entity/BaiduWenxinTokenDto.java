package com.citc.nce.im.session.entity;

import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/9/5 15:24
 */
@Data
public class BaiduWenxinTokenDto {
    private String refreshToken;
    // 过期时间，单位毫秒
    private String expiresIn;
    private String sessionKey;
    // 鉴权token
    private String accessToken;
    private String scope;
    private String sessionSecret;
    // 错误码
    private String error;
    // 错误描述信息
    private String errorDescription;
}
