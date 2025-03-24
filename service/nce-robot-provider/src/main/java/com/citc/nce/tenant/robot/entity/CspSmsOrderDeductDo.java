package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@TableName("csp_sms_order_deduct")
@Data
public class CspSmsOrderDeductDo extends BaseDo<CspSmsOrderDeductDo> implements Serializable {

    /**
     * 订单编号
     */
    @TableField(value = "order_id")
    private String orderId;
    /**
     * 客户id
     */
    @TableField(value = "chatbot_account_id")
    private String chatbotAccountId;
    /**
     * 账号id
     */
    @TableField(value = "account_id")
    private String accountId;

    /**
     * 退款金额
     */
    @TableField(value = "refund_amount")
    private BigDecimal refundAmount;
    /**
     * 扣除条数
     */
    @TableField(value = "deduct_count")
    private Long deductCount;
    /**
     * 0:已扣除 1:已撤回
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
