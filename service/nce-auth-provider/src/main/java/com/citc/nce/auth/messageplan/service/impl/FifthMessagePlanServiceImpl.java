package com.citc.nce.auth.messageplan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.messageplan.dao.FifthMessagePlanMapper;
import com.citc.nce.auth.messageplan.entity.FifthMessagePlan;
import com.citc.nce.auth.messageplan.enums.MessagePlanStatus;
import com.citc.nce.auth.messageplan.service.IFifthMessagePlanService;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanAddVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanListVo;
import com.citc.nce.auth.messageplan.vo.FifthMessagePlanVo;
import com.citc.nce.auth.messageplan.vo.MessagePlanSelectReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.citc.nce.auth.utils.MsgPaymentUtils.formatPrice;

/**
 * <p>
 * 5G消息套餐表 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-23 03:01:41
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FifthMessagePlanServiceImpl extends ServiceImpl<FifthMessagePlanMapper, FifthMessagePlan> implements IFifthMessagePlanService {

    /**
     * 新增5g消息套餐
     *
     * @param addVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPlan(FifthMessagePlanAddVo addVo) {
        if (addVo.getRichMessageNumber() + addVo.getTextMessageNumber() + addVo.getConversionNumber() <= 0)
            throw new BizException("消息条数之和必须大于0");
        if (addVo.getOperator() == CSPOperatorCodeEnum.DEFAULT) {
            long defaultMessageNumber = 999999;
            BigDecimal defaultPrice = BigDecimal.ZERO;
            addVo.setTextMessageNumber(defaultMessageNumber);
            addVo.setTextMessagePrice(defaultPrice);
            addVo.setRichMessageNumber(defaultMessageNumber);
            addVo.setRichMessagePrice(defaultPrice);
            addVo.setConversionNumber(defaultMessageNumber);
            addVo.setConversionPrice(defaultPrice);
        }
        if (exists(new LambdaQueryWrapper<FifthMessagePlan>().eq(FifthMessagePlan::getName, addVo.getName()))) {
            throw new BizException("套餐名称重复");
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        FifthMessagePlan messagePlan = new FifthMessagePlan()
                .setName(addVo.getName())
                .setPlanId("MP" + df.format(LocalDateTime.now()) + RandomUtils.nextInt(10))
                .setOperator(addVo.getOperator())
                .setTextMessageNumber(addVo.getTextMessageNumber())
                .setTextMessagePrice(formatPrice(addVo.getTextMessagePrice()))
                .setRichMessageNumber(addVo.getRichMessageNumber())
                .setRichMessagePrice(formatPrice(addVo.getRichMessagePrice()))
                .setConversionNumber(addVo.getConversionNumber())
                .setConversionPrice(formatPrice(addVo.getConversionPrice()))
                .setStatus(MessagePlanStatus.ON_SHELVES);
        BigDecimal textMessageAmount = BigDecimal.valueOf(messagePlan.getTextMessageNumber()).multiply(messagePlan.getTextMessagePrice());
        BigDecimal richMessageAmount = BigDecimal.valueOf(messagePlan.getRichMessageNumber()).multiply(messagePlan.getRichMessagePrice());
        BigDecimal conversationAmount = BigDecimal.valueOf(messagePlan.getConversionNumber()).multiply(messagePlan.getConversionPrice());
        messagePlan.setAmount(textMessageAmount.add(richMessageAmount).add(conversationAmount).stripTrailingZeros().toPlainString());
        this.save(messagePlan);
    }

    /**
     * 分页查询消息套餐
     */
    @Override
    public Page<FifthMessagePlanListVo> selectPlan(MessagePlanSelectReq selectReq) {
        Page<FifthMessagePlanListVo> page = new Page<>(selectReq.getPageNo(), selectReq.getPageSize());
        page.setOrders(OrderItem.descs("create_time"));
        MessagePlanStatus status = null;
        if (SessionContextUtil.getUser().getIsCustomer())  //客户只能看到已上架的计划
            status = MessagePlanStatus.ON_SHELVES;
        return baseMapper.selectPlan(selectReq.getOperator(), selectReq.getPlanId(), SessionContextUtil.getUser().getCspId(), status, page);
    }

    /**
     * 修改套餐上下架状态
     *
     * @param id
     * @param status
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateState(Long id, Integer status) {
        if (Arrays.stream(MessagePlanStatus.values()).noneMatch(state -> state.getCode().equals(status)))
            throw new BizException("invalid status");
        boolean updated = this.lambdaUpdate()
                .eq(FifthMessagePlan::getId, id)
                .ne(FifthMessagePlan::getStatus, status)
                .eq(FifthMessagePlan::getCreator, SessionContextUtil.getUser().getCspId())
                .set(FifthMessagePlan::getStatus, status)
                .update();
        if (!updated)
            throw new BizException("更新失败");
    }

    /**
     * 删除套餐
     *
     * @param id 要删除的套餐ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deletePlan(Long id) {
        boolean removed = this.remove(
                Wrappers.<FifthMessagePlan>lambdaQuery()
                        .eq(FifthMessagePlan::getId, id)
                        .eq(FifthMessagePlan::getStatus, MessagePlanStatus.OFF_SHELVES)
                        .eq(FifthMessagePlan::getCreator, SessionContextUtil.getUser().getCspId())
        );
        if (!removed)
            throw new BizException("删除失败");
    }

    /**
     * 查询套餐详情
     *
     * @param id 要查询的套餐ID
     */
    @Override
    public FifthMessagePlanVo getPlanDetail(Long id) {
        return baseMapper.selectPlanDetail(id, SessionContextUtil.getLoginUser().getCspId());
    }
}
