package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserInfo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String name;

    @ApiModelProperty(value = "用户头像UUID")
    private String userImgUuid;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "电子邮箱")
    private String mail;

    @ApiModelProperty(value = "邮箱是否被激活 0未激活 1已激活")
    private Integer emailActivated;

    @ApiModelProperty(value = "用户类型：0 个人用户 1 企业用户")
    private Integer userType;

    @ApiModelProperty(value = "个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer personAuthStatus;

    @ApiModelProperty(value = "企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer enterpriseAuthStatus;

    @ApiModelProperty(value = "最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer authStatus;

    @ApiModelProperty(value = "违规次数")
    private Integer unruleNum;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;

    @ApiModelProperty(value = "平台权限(0初始化 默认未开启,1启用,2禁用)")
    private Integer userStatus;

    @ApiModelProperty(value = "扩展商城权限(0禁用,1启用)")
    private Integer tempStorePerm;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "申请审核状态(1 待审核 2 审核不通过 3 审核通过)")
    private Integer approvalStatus;

    @ApiModelProperty(value = "平台信息(1核能商城 2硬核桃 3chatbot)")
    private Integer protal;

    @ApiModelProperty(value = "审核记录id")
    private String approvalLogId;

    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    @ApiModelProperty(value = "服务商logo")
    private String spLogo;

    @ApiModelProperty(value = "服务商电话")
    private String spTel;

    @ApiModelProperty(value = "服务商邮件")
    private String spEmail;

    @ApiModelProperty(value = "是否设置服务商信息")
    private Boolean isSetSp;

    @ApiModelProperty(value = "是否禁用")
    private Boolean isDisable;

    @ApiModelProperty(value = "是否为能力提供商")
    private Boolean isProvider;

    @ApiModelProperty(value = "是否有处理记录")
    private Boolean hasRecords;

    @ApiModelProperty(value = "资质ID")
    private Integer certificateId;

    @ApiModelProperty(value = "账户资质")
    private String userCertificate;

    @ApiModelProperty(value = "账户资质id数组")
    private String certificateIds;

    @ApiModelProperty(value = "csp状态，0-禁用，1-激活")
    private Integer cspActive;

    @ApiModelProperty(value = "电信通道（1-直连 2-蜂动）")
    private Integer telecomChannel;

    @ApiModelProperty(value = "移动通道（1-直连 2-蜂动）")
    private Integer mobileChannel;

    @ApiModelProperty(value = "联通通道（1-直连 2-蜂动）")
    private Integer unicomChannel;

    @ApiModelProperty(value = "cspId")
    private String cspId;

    @ApiModelProperty(value = "csp套餐客户数量状态(0正常,1异常)")
    private Integer cspMealStatus;

    @ApiModelProperty(value = "csp实际客户数")
    private Long cspCustomerNum;

    @ApiModelProperty(value = "csp合同套餐客户数")
    private Long cspMealCustomerNum;
}
