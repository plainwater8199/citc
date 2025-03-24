package com.citc.nce.auth.user.vo.resp;

import com.citc.nce.common.core.pojo.BaseUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @authoer:ldy
 * @createDate:2022/7/3 1:48
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "门户页登录用户信息")
public class WebUserLoginResp extends BaseUser {

    private Long id;

    @ApiModelProperty(value = "用户名", dataType = "String")
    private String name;

    @ApiModelProperty(value = "token", dataType = "String")
    private String token;

    @ApiModelProperty(value = "邮箱", dataType = "String")
    private String mail;

    @ApiModelProperty(value = "邮箱激活状态 0未激活 1已激活", dataType = "Integer")
    private Integer emailActivated;

    @ApiModelProperty(value = "用户图像对应的uuid", dataType = "String")
    private String userImgUuid;

    @ApiModelProperty(value = "用户类型：0 个人用户 1 企业用户", dataType = "Integer")
    private Integer userType;

    @ApiModelProperty(value = "个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer personAuthStatus;

    @ApiModelProperty(value = "企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer enterpriseAuthStatus;

    @ApiModelProperty(value = "最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer authStatus;

    @ApiModelProperty(value = "注册时间", dataType = "Date")
    private Date registerTime;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;

    @ApiModelProperty(value = "企业认证备注", dataType = "String")
    private String auditRemark;

    @ApiModelProperty(value = "平台使用权限申请审核状态(1 待审核 2 审核不通过 3 审核通过)", dataType = "Integer")
    private Integer approvalStatus;

    @ApiModelProperty(value = "平台使用权限审核", dataType = "String")
    private String remark;

}
