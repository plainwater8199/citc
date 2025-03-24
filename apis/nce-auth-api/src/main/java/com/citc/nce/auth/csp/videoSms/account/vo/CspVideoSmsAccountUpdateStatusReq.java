package com.citc.nce.auth.csp.videoSms.account.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CspVideoSmsAccountUpdateStatusReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "状态 0:禁用 1:启用")
    private Integer status;

}
