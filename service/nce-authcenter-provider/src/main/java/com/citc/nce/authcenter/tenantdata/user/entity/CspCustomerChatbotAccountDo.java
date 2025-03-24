package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("csp_customer_chatbot_account")
public class CspCustomerChatbotAccountDo implements Serializable {
    private static final long serialVersionUID = -1543935710097855690L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String cspId;

    private String customerId;
    /**
     * customerName  用户名,冗余,方便后台管理查询
     */
    @TableField(value = "customer_name")
    private String customerName;

    private String chatbotAccountId;

    private Long accountManagementId; //TODO fq

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

    @ApiModelProperty("是否已删除")
    private Integer deleted;

    @ApiModelProperty("删除时间")
    private Date deleteTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * chatbot服务提供商tag  蜂动: fontdo   自有chatbot:owner
     */
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
     * 通道
     */
    @TableField(value = "channel")
    private Integer channel;

    /**
     * 向网关同步的状态  默认0 已同步   -1 同步失败
     */
    @TableField(value = "sync_gateway_state")
    private Integer SyncGatewayState;

}
