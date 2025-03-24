package com.citc.nce.authcenter.captcha.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CaptchaResp implements Serializable {

    /**
     * user是user表中的用户 ，csp-ctuId 是csp小号
     */
    @ApiModelProperty(value = "用户身份 user csp-ctuId csp-customer")
    private String userIdentity = "user";

    /**
     * 短信验证码code
     */
    @ApiModelProperty(value = "短信验证码code", dataType = "String")
    private String code;
    /**
     * 验证码关联key，需要在校验时一起传递
     */
    @ApiModelProperty(value = "验证码关联key", dataType = "String")
    private String captchaKey;

    /**
     * 获取code的时间戳
     */
    @ApiModelProperty(value = "验证码时间戳", dataType = "Long")
    private Long timestamp;

    @ApiModelProperty(value = "是否需要勾选法务文件，0：不需要勾选，1：勾选", dataType = "int")
    private int isAgreement = 0;

}
