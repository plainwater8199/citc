package com.citc.nce.authcenter.csp.multitenant.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.common.core.enums.CustomerPayType;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_customer")
public class CspCustomer extends BaseDo<CspCustomer> implements Serializable {

    private static final long serialVersionUID = 571041720613611041L;

    @ApiModelProperty("csp id")
    private String cspId;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名")
    private String name;

    @ApiModelProperty("用户头像UUID")
    private String userImgUuid;

    private String phone;

    private String mail;

    private Integer emailActivated;

    /**
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer personAuthStatus;

    /**
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer enterpriseAuthStatus;

    /**
     * 最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer authStatus;

    private String province;

    private String provinceCode;

    private String city;

    private String cityCode;

    private String area;

    private String areaCode;

    private Boolean customerActive;

    private String permissions;

    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("使用过期时间")
    private Date outOfTime;

    @ApiModelProperty("客户付费方式")
    private CustomerPayType payType;

    @ApiModelProperty("允许服务商代理登录")
    private Boolean enableAgentLogin;

    @ApiModelProperty("账号余额")
    private Long balance;
}
