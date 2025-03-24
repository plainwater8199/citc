package com.citc.nce.authcenter.auth.vo.resp;

import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.prepayment.vo.CustomerPrepaymentConfigVo;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class QueryUserInfoDetailResp {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("小号id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ctuId;

    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "用户账户名", dataType = "String")
    private String userName;

    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    @ApiModelProperty(value = "用户平台类型:1 核能商城客户端、2 chatbot客户端、3 硬核桃社区、4 管理平台", dataType = "String")
    private Integer platformType;

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

    @ApiModelProperty(value = "用户的标签列表：10001-企业用户，10002-实名用户，10003-入驻用户，10004-能力提供商，10005-解决方案商，10006-CSP用户", dataType = "List")
    private List<String> certificateList;

    private String permissions;

    @ApiModelProperty("登录地址")
    private String loginAddress;

    @ApiModelProperty("客户付费方式")
    private CustomerPayType payType;

    @ApiModelProperty("后付费配置")
    private CustomerPostpayConfigVo postpayConfig;

    @ApiModelProperty("预付费配置")
    private CustomerPrepaymentConfigVo prepaymentConfig;

    private Boolean isCustomer = false;

    @ApiModelProperty("通道")
    private ChannelInfoResp channel;

    @ApiModelProperty("允许服务商代理登录")
    private Boolean enableAgentLogin;

    @ApiModelProperty("是否是服务商代理登录")
    private Boolean isAgentLogin;

    @ApiModelProperty("账户余额")
    private Long balance;

    /**
     * 扩展商城权限(0禁用,1启用)
     */
    private Boolean tempStorePerm;
}
