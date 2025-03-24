package com.citc.nce.auth.csp.videoSms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsAccountDeductResidueReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数量")
    private Long num;

    @ApiModelProperty(value = "账号id")
    private String accountId;
}
