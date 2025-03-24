package com.citc.nce.authcenter.captcha.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImageCaptchaResp {
    /**
     * 验证码图片（经过base64，客户端可直接展示）
     * <p>
     * 格式为: data:image/jpg;base64,xxxxxxx...
     */
    @ApiModelProperty(value = "验证码图片", dataType = "String")
    private String captchaImage;

    /**
     * 验证码关联key，需要在校验时一起传递
     */
    @ApiModelProperty(value = "验证码关联key", dataType = "String")
    private String captchaKey;
}
