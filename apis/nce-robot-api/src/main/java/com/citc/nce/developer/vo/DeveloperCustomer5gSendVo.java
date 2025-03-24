package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomer5gSendVo {

    @ApiModelProperty("唯一标识")
    @NotBlank(message = "appId不能为空")
    private String appId;

    @ApiModelProperty("请求时间,时间格式yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "请求时间不能为空")
    private String time;

    @ApiModelProperty("签名md5(appKey+appSecret+AUTH_TIMESTAMP)")
    @NotBlank(message = "签名不能为空")
    private String sign;

    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty("如何是个性模板需要传入变量，变量个数请与模板匹配")
    private List<String> variables;
}
