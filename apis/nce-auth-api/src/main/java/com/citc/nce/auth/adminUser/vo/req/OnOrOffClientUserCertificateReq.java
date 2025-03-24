package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Admin端 启停client端用户资质req")
public class OnOrOffClientUserCertificateReq {

    @ApiModelProperty(value = "client端用户id")
    @NotBlank(message = "客户端用户user_id不能为空")
    private String clientUserId;


    @ApiModelProperty(value = "用户账号资质信息主键id")
    private Long certificateOptionsId;

    @ApiModelProperty(value = "资质code")
    @NotNull(message = "资质code不能为空")
    private Integer certificateOptionsCode;

    @ApiModelProperty(value = " 资质状态(0开启,1关闭)")
    @NotNull(message = "资质状态不能为空")
    private Integer certificateStatus;

    @ApiModelProperty(value = "备注")
    private String remark;
}
