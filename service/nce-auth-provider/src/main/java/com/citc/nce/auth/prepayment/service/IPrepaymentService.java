package com.citc.nce.auth.prepayment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.prepayment.vo.BaseMessageAccountListVo;
import com.citc.nce.auth.prepayment.vo.CustomerAccountPrepaymentListVo;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.auth.prepayment.vo.MessagePlanInfoDto;

import java.util.List;

/**
 * 预支付service
 *
 * @author jiancheng
 */
public interface IPrepaymentService {
    /**
     * 校验账号是否合法
     * 1. 必须存在并属于customerId
     *
     * @param account    要校验的账号ID
     * @param customerId 要校验的账号的用户
     * @throws com.citc.nce.common.core.exception.BizException 当账号不合法时抛出此异常
     */
    void verifyAccount(String customerId, String account);

    /**
     * 1. 校验套餐（必须是customerId所属csp创建）
     * 2. 计算套餐总金额
     * 3. 生成套餐快照
     * 4. 生成套餐明细
     *
     * @param customerId 要购买套餐的客户ID
     * @param planId     套餐ID
     * @return 套餐明细
     * @throws com.citc.nce.common.core.exception.BizException 当套餐不合法时抛出此异常
     */
    MessagePlanInfoDto getPlanInfo(String customerId, String planId);


}
