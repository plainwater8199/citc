package com.citc.nce.authcenter.auth.vo.resp;

import lombok.Data;

@Data
public class UserInfo {
    private Long id;
    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名 collate utf8mb4_0900_ai_ci
     */
    private String name;

    /**
     * 用户图像对应的uuid
     */
    private String userImgUuid;

    /**
     * 手机号 collate utf8mb4_0900_ai_ci
     */
    private String phone;

    /**
     * 电子邮箱 collate utf8mb4_0900_ai_ci
     */
    private String mail;

    /**
     * 邮箱是否被激活 0未激活 1已激活
     */
    private Integer emailActivated;

    /**
     * 用户类型：0 个人用户 1 企业用户
     */
    private Integer userType;

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

    /**
     * 服务商LOGO
     */
    private String spLogo;

    /**
     * 服务商电话
     */
    private String spTel;

    /**
     * 服务商邮箱
     */
    private String spEmail;

    /**
     * 企业名称
     */
    private String enterpriseName;
}
