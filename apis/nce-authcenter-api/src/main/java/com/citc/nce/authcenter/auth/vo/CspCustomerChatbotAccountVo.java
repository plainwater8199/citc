package com.citc.nce.authcenter.auth.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import java.util.Date;

/**
 * 文件名:CspCustomerChatbotAccountVo
 * 创建者:zhujinyu
 * 创建时间:2024/3/16 13:46
 * 描述:
 */
@Data
public class CspCustomerChatbotAccountVo {
    private static final long serialVersionUID = -1543935710097855690L;

    private String cspId;

    private String customerId;

    private String customerName;

    private String cspName;

    private String chatbotAccountId;

    private Long accountManagementId; //TODO fq

    private String chatbotAccount;

    private String accountType;

    private String accountName;

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
    private String supplierTag;
    /**
     * 代理商id
     */
    private String agentId;
    /**
     * chatbot服务提供商租户id
     */
    private String ecId;
    /**
     * 通道
     */
    private Integer channel;
}
