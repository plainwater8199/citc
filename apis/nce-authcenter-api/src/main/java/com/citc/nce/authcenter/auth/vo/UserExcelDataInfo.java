package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserExcelDataInfo {
    @ApiModelProperty(value = "账户名")
    private String account;
    /**
     * 账号ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 违规次数
     */
    @ApiModelProperty(value = "违规次数")
    private Integer unruleNum;
    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;
    /**
     * 邮箱
     */
    @ApiModelProperty(value = " 邮箱")
    private String mail;
    /**
     * 注册时间
     */
    @ApiModelProperty(value = "注册时间")
    private Date registerTime;
    /**
     * 账户资质
     */
    @ApiModelProperty(value = "账户资质")
    private String userCertificate;

    /**
     * 账户资质
     */
    @ApiModelProperty(value = "账户资质code")
    private String certificateId;


    @ApiModelProperty(value = "认证状态: 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer userCertificateApplyStatus;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)")
    private Integer userStatus;

    @ApiModelProperty(value = "是否有处理记录")
    private Boolean hasRecords;
}
