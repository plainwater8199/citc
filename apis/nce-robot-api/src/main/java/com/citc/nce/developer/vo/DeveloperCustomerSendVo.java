package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ping chen
 */
@Data
public class DeveloperCustomerSendVo {

    @ApiModelProperty("公钥")
    @NotBlank(message = "公钥不能为空")
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

    @ApiModelProperty("平台模板Id")
    @NotNull(message = "模板Id不能为空")
    private String platformTemplateId;

    @ApiModelProperty("如何是个性模板需要传入变量，变量个数请与模板匹配")
    private List<String> variables;
}
