package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DisposeQualificationApplyReq implements Serializable {

    @NotNull(message = "主键id不能为空")
    @ApiModelProperty(value = "表id", dataType = "Long", required = true)
    private Long id;

    @Length(max = 200, message = "备注长度超过限制")
    @ApiModelProperty(value = "备注", dataType = "String")
    private String remark;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    private String userId;

    @NotNull(message = "申请状态不能为空")
    @ApiModelProperty(value = "资质申请状态(1待审核,2审核通过,3审核不通过)", dataType = "Integer", required = true)
    private Integer certificateApplyStatus;

    @NotNull(message = "资质名称不能为空")
    @ApiModelProperty(value = "资质名称", dataType = "Integer", required = true)
    private Integer qualificationName;
}
