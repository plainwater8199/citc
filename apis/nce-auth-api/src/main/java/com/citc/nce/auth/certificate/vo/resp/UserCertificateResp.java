package com.citc.nce.auth.certificate.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "Admin端 查看client端用户资质resp")
public class UserCertificateResp {

    /**
     * 用户账号资质信息主键id
     */
    @ApiModelProperty(value = "user_certificate_options表主键id")
    private Long id;

    /**
     * 资质id
     */
    @ApiModelProperty(value = "资质id")
    private Long certificateId;

    /**
     * 用户id collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "client端用户id")
    private String userId;

    @ApiModelProperty(value = "资质申请状态(1待审核,2审核不通过,3审核通过)")
    private Integer certificateApplyStatus;

    @ApiModelProperty(value = "资质状态(0开启，1关闭，2无状态)")
    private Integer certificateStatus;

    /**
     * 资质名称 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "资质名称")
    private String certificateName;

    /**
     * 资质图标 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = " 资质图标")
    private String certificateImg;

    /**
     * 资质描述 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "资质描述 ")
    private String detail;

    /**
     * 平台信息(1硬核桃2核能商城)
     */
    @ApiModelProperty(value = "平台信息(1硬核桃2核能商城)")
    private Integer protal;


}
