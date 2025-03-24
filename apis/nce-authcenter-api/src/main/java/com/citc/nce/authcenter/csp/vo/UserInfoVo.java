package com.citc.nce.authcenter.csp.vo;

import com.citc.nce.common.core.enums.CustomerPayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jiancheng
 */
@Data
public class UserInfoVo {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty("用户数据")
    private String userId;

    @ApiModelProperty("csp id")
    private String cspId;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("邮箱")
    private String mail;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("账户名")
    private String name;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业名称-唯一")
    private String enterpriseAccountName;

    @ApiModelProperty("客户是否启用")
    private Boolean customerActive;

    @ApiModelProperty("客户付费方式")
    private CustomerPayType payType;

    @ApiModelProperty(value = "功能权限 1、群发；2、机器人")
    private String permissions;

    @ApiModelProperty(value = "扩展商城权限(0禁用,1启用)")
    private Integer tempStorePerm;

    @ApiModelProperty("充值余额")
    private Long balance;
}
