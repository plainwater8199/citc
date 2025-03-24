package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.math.BigDecimal;

@TableName("csp_sms_order_recharge")
@Data
public class CspSmsOrderRechargeDo extends BaseDo<CspSmsOrderRechargeDo> {

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;

    /**
     * 账号id
     */
    @TableField(value = "account_id")
    private String accountId;

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


    @TableField(value = "customer_id")
    private String customerId;

    private String creatorOld;

    private String updaterOld;
    @TableField(value = "user_id")
    private String userId;

}
