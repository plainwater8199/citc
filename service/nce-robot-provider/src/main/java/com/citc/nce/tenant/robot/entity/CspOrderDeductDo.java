package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单扣除表(CspOrderDeduct)实体类
 *
 * @author makejava
 * @since 2023-08-17 09:38:18
 */
@TableName("csp_order_deduct")
@Data
public class CspOrderDeductDo extends BaseDo<CspOrderDeductDo> implements Serializable {
    private static final long serialVersionUID = -59615022877910981L;

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
    private String accountId; //TODO fq

    private String chatbotAccountId;
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


    @TableField(value = "user_id")
    private String userId; //TODO fq

    private String creatorOld;

    private String updaterOld;
}

