package com.citc.nce.auth.csp.mediasms.template.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsHaveTemplateAccountVo {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("账户名称")
    private String accountName;

    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("状态 0:禁用 1:启用")
    private Integer status;
}
