package com.citc.nce.auth.postpay.order.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.csp.recharge.service.ChargeConsumeStatisticService;
import com.citc.nce.auth.postpay.order.dao.PostpayOrderDetailMapper;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailReq;
import com.citc.nce.auth.postpay.order.vo.PostpayOrderDetailVo;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/3/7 16:15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostpayOrderDetailService extends ServiceImpl<PostpayOrderDetailMapper, PostpayOrderDetail> implements IService<PostpayOrderDetail> {
    @Resource
    ChargeConsumeStatisticService chargeConsumeStatisticService;
    @SuppressWarnings("unchecked")
    public List<PostpayOrderDetail> listByOrderId(String orderId) {
        List<PostpayOrderDetail> packageDetail= this.lambdaQuery()
                .eq(PostpayOrderDetail::getOrderId, orderId)
                .orderByAsc(PostpayOrderDetail::getMsgType, PostpayOrderDetail::getMsgSubType)
                .list();
        if(ObjUtil.isNull(packageDetail))
            packageDetail=new ArrayList<>();
        packageDetail.forEach(item->item.setConsumeCategory(PaymentTypeEnum.SET_MEAL.getCode()));
        List<PostpayOrderDetail> chargeDetail=chargeConsumeStatisticService.getStatisticDetailDo(orderId);
        if(ObjUtil.isNotNull(chargeDetail))
        {
            chargeDetail.forEach(item->item.setConsumeCategory(PaymentTypeEnum.BALANCE.getCode()));
            packageDetail.addAll(chargeDetail);
        }
        return packageDetail;
    }

    /**
     * 查询订单详情
     */
    public List<PostpayOrderDetailVo> selectOrderDetail(PostpayOrderDetailReq req) {
        List<PostpayOrderDetailVo> packageDetail= baseMapper.selectOrderDetail(req.getOrderId());
        if(ObjUtil.isNull(packageDetail))
            packageDetail=new ArrayList<>();
        packageDetail.forEach(item->item.setConsumeCategory(PaymentTypeEnum.SET_MEAL.getCode()));
        List<PostpayOrderDetailVo> chargeDetail=chargeConsumeStatisticService.getDetail(req.getOrderId());
        if(ObjUtil.isNotNull(chargeDetail))
        {
            chargeDetail.forEach(item->item.setConsumeCategory(PaymentTypeEnum.BALANCE.getCode()));
            packageDetail.addAll(chargeDetail);
        }
        return packageDetail;
    }

}
