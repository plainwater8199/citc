package com.citc.nce.auth.certificate.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/15
 * @Version 1.0
 * @Description:
 */
@Data
public class DisposeCertificateOptionsReq {
    @NotNull(message = "申请状态不能为空")
    @ApiModelProperty(value = "资质申请状态(1待审核,2审核通过,3审核不通过)", dataType = "Integer", required = true)
    private Integer certificateApplyStatus;

    @NotNull(message = "资质名称不能为空")
    @ApiModelProperty(value = "资质名称", dataType = "Integer", required = true)
    private Integer qualificationName;
}
