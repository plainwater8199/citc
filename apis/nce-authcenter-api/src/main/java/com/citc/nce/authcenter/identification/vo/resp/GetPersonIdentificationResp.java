package com.citc.nce.authcenter.identification.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class GetPersonIdentificationResp {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "个人认证状态", dataType = "Integer")
    private Integer personAuthStatus;

    @ApiModelProperty(value = "用户姓名", dataType = "String")
    private String personName;

    @ApiModelProperty(value = "身份证编号", dataType = "String")
    private String idCard;

    /**
     * 身份证正面照片
     */
    @ApiModelProperty(value = "身份证正面照片", dataType = "String")
    private String idCardImgFront;

    /**
     * 身份证反面照片
     */
    @ApiModelProperty(value = "身份证反面照片", dataType = "String")
    private String idCardImgBack;

    /**
     * 个人实名认证申请时间
     */
    @ApiModelProperty(value = "个人实名认证申请时间", dataType = "Date")
    private Date personAuthTime;

    /**
     * 审核备注信息
     */
    @ApiModelProperty(value = "审核备注信息", dataType = "String")
    private String auditRemark;

    /**
     *  protal 平台信息(1核能商城2硬核桃3chatbot)
     * */
    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer")
    private Integer protal;
}
