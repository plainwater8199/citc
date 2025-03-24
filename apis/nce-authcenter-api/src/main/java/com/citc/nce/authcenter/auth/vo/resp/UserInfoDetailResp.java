package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户基本信息resp
 *
 * @Author: huangchong
 */
@Data
@Accessors(chain = true)
public class UserInfoDetailResp implements Serializable {

    @ApiModelProperty(value = "id", dataType = "String")
    private Long id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    /**
     * 用户名 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "用户名", dataType = "String")
    private String name;

    @ApiModelProperty(value = "token", dataType = "String")
    private String token;
    /**
     * 用户图像对应的uuid
     */
    @ApiModelProperty(value = "用户图像对应的uuid", dataType = "String")
    private String userImgUuid;

    /**
     * 手机号 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    /**
     * 电子邮箱 collate utf8mb4_0900_ai_ci
     */
    @ApiModelProperty(value = "电子邮箱", dataType = "String")
    private String mail;

    /**
     * 邮箱是否被激活 0未激活 1已激活
     */
    @ApiModelProperty(value = "邮箱是否被激活 0未激活 1已激活", dataType = "Integer")
    private Integer emailActivated;

    /**
     * 用户类型：0 个人用户 1 企业用户
     */
    @ApiModelProperty(value = "用户类型：0 个人用户 1 企业用户", dataType = "Integer")
    private Integer userType;

    /**
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer personAuthStatus;

    /**
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer enterpriseAuthStatus;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称", dataType = "String")
    private String enterpriseName;

    /**
     * 最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    @ApiModelProperty(value = "最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过", dataType = "Integer")
    private Integer authStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "注册时间", dataType = "Date")
    private Date registerTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "商户联系方式", dataType = "String")
    private String spTel;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "商户邮件", dataType = "String")
    private String spEmail;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "商户logo", dataType = "String")
    private String spLogo;

    /**
     * 用户在各个平台的状态
     */
    @ApiModelProperty(value = "用户在各个平台的状态", dataType = "List")
    private List<StatusAndProtalResp> userStatusList;
}
