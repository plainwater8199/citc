package com.citc.nce.auth.accountmanagement.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 16:55
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("csp_customer_chatbot_account")
public class AccountManagementDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属客户Id
     */
    @TableField(value = "customer_id")
    private String customerId;

    /**
     * 机器人创建者(csp)
     */
    @TableField(value = "csp_id")
    private String cspId;

    /**
     * 账号ID(唯一标识)
     */
    @TableField(value = "chatbot_account_id")
    private String chatbotAccountId;

    /**
     * chatbot账号
     */
    @TableField(value = "chatbot_account")
    private String chatbotAccount;

    /**
     * 账户类型，1联通2硬核桃
     * 2023/2/20 注意：数据库存的名字，不是数字类型
     */
    @TableField(value = "account_type")
    private String accountType;

    /**
     * 账号名称
     */
    @TableField(value = "account_name")
    private String accountName;


    /**
     * appid
     */
    @TableField(value = "app_id")
    private String appId;

    /**
     * appkey
     */
    @TableField(value = "app_key")
    private String appKey;

    /**
     * token
     */
    @TableField(value = "token")
    private String token;

    /**
     * 消息地址
     */
    @TableField(value = "message_address")
    private String messageAddress;

    /**
     * 文件地址
     */
    @TableField(value = "file_address")
    private String fileAddress;

    /**
     * 0未删除  1已删除
     */
    @TableField(value = "deleted")
    private Integer deleted;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    /**
     * 机器人状态(30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试)
     */
    @TableField(value = "chatbot_status")
    private Integer chatbotStatus;

    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    @TableField(value = "account_type_code")
    private Integer accountTypeCode;

    /**
     * 机器人新增方式 0：本地添加，1：线上合同添加
     */
    @TableField(value = "is_add_other")
    private Integer isAddOther;
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
     * 通道 1  csp自己的  2 蜂动
     */
    @TableField(value = "channel")
    private Integer channel;

    /**
     * customerName  用户名,冗余,方便后台管理查询
     */
    @TableField(value = "customer_name")
    private String customerName;

    /**
     * tariffBatch  当前资费批次，用于查询chatbot资费
     */
    @TableField(value = "tariff_batch")
    private String tariffBatch;

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
}
