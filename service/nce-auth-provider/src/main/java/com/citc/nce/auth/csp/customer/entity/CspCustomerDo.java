package com.citc.nce.auth.csp.customer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("csp_customer")
public class CspCustomerDo extends BaseDo<CspCustomerDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 客户Id
     */
    private String customerId;
    /**
     * cspId
     */
    private String cspId;
    /**
     * cspId
     */
    private String name;
    /**
     * 用户头像UUID
     */
    private String userImgUuid;
    /**
     * 密码
     */
    private String password;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 电子邮箱
     */
    private String mail;
    /**
     * email_activated
     */
    private Integer emailActivated;

    private Integer enterpriseAuthStatus;

    private Integer personAuthStatus;
    /**
     * 所在省
     */
    private String province;
    /**
     * 所在省Code
     */
    private String provinceCode;
    /**
     * 所在市
     */
    private String city;
    /**
     * 所在市Code
     */
    private String cityCode;
    /**
     * 所在地区
     */
    private String area;
    /**
     * 所在地区Code
     */
    private String areaCode;
    /**
     * CSP启用状态 0:未启用 1：已启用
     */
    private Integer customerActive;
    /**
     * 用户功能权限(1.群发；2机器人; 3视频短信  用逗号分割)
     */
    private String permissions;

    private int deleted;

    private Long deletedTime;
    /**
     * 是否绑定合同
     */
    private Integer isBinding;
    /**
     * 是否绑定chatbot
     */
    private Integer isBindingChatbot;
}
