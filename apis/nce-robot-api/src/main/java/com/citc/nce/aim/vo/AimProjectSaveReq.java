package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimProjectSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目名称", dataType = "String", required = true)
    @NotBlank(message = "项目名称不能为空")
    @Length(max = 20, message = "项目名称长度超过限制(最大20位)")
    private String projectName;

    @ApiModelProperty(value = "客户号码", dataType = "String", required = true)
    @NotBlank(message = "客户号码不能为空")
    @Length(max = 15, message = "客户号码长度超过限制(最大15位)")
    private String calling;

    @ApiModelProperty(value = "通道账号", dataType = "String", required = true)
    @NotBlank(message = "通道账号不能为空")
    @Length(max = 100, message = "通道账号长度超过限制(最大100位)")
    private String pathKey;

    @ApiModelProperty(value = "通道秘钥", dataType = "String", required = true)
    @NotBlank(message = "通道秘钥不能为空")
    @Length(max = 100, message = "通道秘钥长度超过限制(最大100位)")
    private String secret;

    @ApiModelProperty(value = "短信内容", dataType = "String", required = true)
    @NotBlank(message = "短信内容不能为空")
    @Length(max = 70, message = "短信内容长度超过限制(最大70位)")
    private String smsTemplate;
}
