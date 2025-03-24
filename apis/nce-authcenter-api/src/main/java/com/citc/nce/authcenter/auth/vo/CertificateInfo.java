package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description 资历信息
 * @date 2022/8/27 09:13:39
 */
@Data
public class CertificateInfo {
    @ApiModelProperty(value = "资历id")
    private Integer certificateId;
    @ApiModelProperty(value = "资历名称")
    private String certificateName;
    @ApiModelProperty(value = "资历状态")
    private Integer certificateApplyStatus;
    @ApiModelProperty(value = "资历logo")
    private String certificateImg;
}
