package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单充值表(CspOrderRecharge)实体类
 *
 * @author makejava
 * @since 2023-08-15 11:20:55
 */
@TableName("csp_order_recharge")
@Data
public class CspOrderRechargeDo extends BaseDo<CspOrderRechargeDo> {

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;
    /**
     * 客户id
     */
    @TableField(value = "customer_id")
    private String customerId;
    /**
     * 账号id
     */
    @TableField(value = "account_id")
    private String accountId;//TODO fq

    private String chatbotAccountId;
    /**
     * 充值金额
     */
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;
    /**
     * 充值条数
     */
    @TableField(value = "recharge_count")
    private Long rechargeCount;
    /**
     * 0:已充值 1:已撤回
     */
    @TableField(value = "order_status")
    private Integer orderStatus;
    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;


    @TableField(value = "user_id")
    private String userId; //TODO fq


    private String creatorOld;

    private String updaterOld;

}

