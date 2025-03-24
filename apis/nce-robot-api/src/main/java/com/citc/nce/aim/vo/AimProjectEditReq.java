package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimProjectEditReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id", dataType = "Long", required = true)
    @NotNull(message = "主键id不能为空")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String", required = true)
    @NotBlank(message = "项目Id不能为空")
    private String projectId;

    @ApiModelProperty(value = "项目名称", dataType = "String")
    @Length(max = 20, message = "项目名称长度超过限制(最大20位)")
    private String projectName;

    @ApiModelProperty(value = "客户号码", dataType = "String")
    @Length(max = 15, message = "客户号码长度超过限制(最大15位)")
    private String calling;

    @ApiModelProperty(value = "通道账号", dataType = "String")
    @Length(max = 100, message = "通道账号长度超过限制(最大100位)")
    private String pathKey;

    @ApiModelProperty(value = "通道秘钥", dataType = "String")
    @Length(max = 100, message = "通道秘钥长度超过限制(最大100位)")
    private String secret;

    @ApiModelProperty(value = "短信内容", dataType = "String")
    @Length(max = 70, message = "短信内容长度超过限制(最大70位)")
    private String smsTemplate;
}
