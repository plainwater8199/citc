package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.orderRefund.vo.ResidueInfo;
import com.citc.nce.auth.prepayment.dao.PrepaymentOrderDetailMapper;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrderDetail;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderDetailService;
import com.citc.nce.auth.prepayment.vo.MessagePlanDetailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 预付费订单详情表 服务实现类
 * </p>
 *
 * @author jcrenc
 * @since 2024-01-25 10:01:03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrepaymentOrderDetailServiceImpl extends ServiceImpl<PrepaymentOrderDetailMapper, PrepaymentOrderDetail> implements IPrepaymentOrderDetailService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrderDetails(String orderId, List<MessagePlanDetailDto> planDetails) {
        List<PrepaymentOrderDetail> details = planDetails.stream()
                .map(dto -> {
                    PrepaymentOrderDetail detail = new PrepaymentOrderDetail();
                    detail.setOrderId(orderId)
                            .setMsgType(dto.getMsgType())
                            .setMsgSubType(dto.getMsgSubType())
                            .setTotalAmount(dto.getLimit())
                            .setUsedAmount(0L)
                            .setExpiredAmount(0L)
                            .setAvailableAmount(dto.getLimit())
                            .setIsDepleted(false);
                    return detail;
                })
                .collect(Collectors.toList());
        this.saveBatch(details);
    }

    @Override
    public List<PrepaymentOrderDetail> listByOrderId(String orderId) {
        return lambdaQuery().eq(PrepaymentOrderDetail::getOrderId, orderId).list();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ResidueInfo> refundAll(String order) {
        //根据订单id查询当前需要退款的详情
        List<PrepaymentOrderDetail> details = listByOrderId(order);
        //封装当前退款数据
        List<ResidueInfo> residueInfo = getResidueInfo(details);
        //修改数据库退款数量
        for (PrepaymentOrderDetail detail : details) {
            Long availableAmount = detail.getAvailableAmount();
            Long expiredAmount = detail.getExpiredAmount();
            detail.setExpiredAmount(Math.addExact(availableAmount, expiredAmount));
            detail.setAvailableAmount(0L);
            detail.setIsDepleted(true);
        }
        updateBatchById(details);
        //返回退款前用户使用量详情
        return residueInfo;
    }

    @Override
    public List<ResidueInfo> getResidueInfo(String orderId) {
        return getResidueInfo(listByOrderId(orderId));
    }

    private List<ResidueInfo> getResidueInfo(List<PrepaymentOrderDetail> details) {
        if (CollectionUtils.isEmpty(details)) return new ArrayList<>();
        return details.stream().map(s -> {
            ResidueInfo residueInfo = new ResidueInfo();
            BeanUtils.copyProperties(s, residueInfo);
            residueInfo.setLimit(s.getTotalAmount());
            residueInfo.setUsage(s.getUsedAmount());
            residueInfo.setInvalid(s.getExpiredAmount());
            residueInfo.setUsable(s.getAvailableAmount());
            return residueInfo;
        }).collect(Collectors.toList());
    }
}
