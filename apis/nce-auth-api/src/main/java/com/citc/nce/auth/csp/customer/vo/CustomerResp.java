package com.citc.nce.auth.csp.customer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerResp implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("企业账户名")
    private String enterpriseAccountName;

    private String mail;

    private String enterpriseLicense;

    @ApiModelProperty("企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过 4 已禁用")
    private Integer enterpriseAuthStatus;

    @ApiModelProperty("CSP启用状态 0:未启用 1：已启用")
    private Integer cspActive;

    @ApiModelProperty("统一社会信用代码/注册号/组织机构代码")
    private String creditCode;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("姓名")
    private String personName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("账户名")
    private String accountName;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("是否绑定")
    private Integer isBinding;

    @ApiModelProperty("是否绑定chatbot")
    private Integer isBindingChatbot;

}
