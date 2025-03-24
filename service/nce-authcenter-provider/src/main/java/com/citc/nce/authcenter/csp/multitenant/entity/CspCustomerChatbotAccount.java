package com.citc.nce.authcenter.csp.multitenant.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_customer_chatbot_account")
public class CspCustomerChatbotAccount extends BaseDo<CspCustomerChatbotAccount> implements Serializable {
    private static final long serialVersionUID = -1543935710097855690L;

    private String cspId;

    private String customerId;

    private String chatbotAccountId;

    private Long accountManagementId;

    private String chatbotAccount;

    private String accountType;

    private String accountName;

    private String appId;

    private String appKey;

    private String token;

    private String messageAddress;

    private String fileAddress;

    private Integer chatbotStatus;

    private Integer accountTypeCode;

    private Integer isAddOther;

    @TableLogic
    private Boolean deleted;

    /**
     * customerName  用户名,冗余,方便后台管理查询
     */
    @TableField(value = "customer_name")
    private String customerName;

    @TableField(value = "supplier_tag")
    private String supplierTag;
    /**
     * 代理商id
     */
    @TableField(value = "agent_id")
    private String agentId;
    /**
     * chatbot服务提供商租户id
     */
    @TableField(value = "ec_id")
    private String ecId;
    /**
     * 通道 1  csp自己的  2 蜂动
     */
    @TableField(value = "channel")
    private Integer channel;


}
