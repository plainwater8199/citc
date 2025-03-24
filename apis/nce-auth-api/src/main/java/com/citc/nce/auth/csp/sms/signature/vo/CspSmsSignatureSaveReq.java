package com.citc.nce.auth.csp.sms.signature.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class CspSmsSignatureSaveReq implements Serializable {

    @ApiModelProperty(value = "主键id,新增时值为null")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "签名")
    @NotBlank(message = "签名不能为空")
    @Length(min = 2, max = 8, message = "签名限制2-8字符")
    private String signature;

    @ApiModelProperty(value = "类型 0:短信 1:其他")
    private Integer type;
}
