package com.citc.nce.misc.captcha.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @authoer:ldy
 * @createDate:2022/7/2 1:28
 * @description: 验证码返回对象，包含key和对应code
 * 在验证码校验是需要传递key
 */
@Data
@Accessors(chain = true)
public class CaptchaResp implements Serializable {

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
}
