package com.citc.nce.auth.postpay.order.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPostPageInfo;
import com.citc.nce.auth.invoice.vo.InvoiceOrderPostQuery;
import com.citc.nce.auth.postpay.order.dao.PostpayOrderMapper;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.postpay.order.enums.PostpayOrderStatus;
import com.citc.nce.auth.postpay.order.vo.*;
import com.citc.nce.auth.utils.SerialNumberUtils;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.DateUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 后付费订单
 *
 * @author jcrenc
 * @since 2024/3/7 16:15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostpayOrderService extends ServiceImpl<PostpayOrderMapper, PostpayOrder> implements IService<PostpayOrder> {
    private final CspCustomerApi cspCustomerApi;
    private final MsgRecordApi msgRecordApi;

    /**
     * csp搜索后付费订单
     *
     * @param req
     * @return
     */
    public PageResult<PostpayOrderVo> searchPostpayOrder(PostpayOrderQueryVo req) {
        String cspId = SessionContextUtil.verifyCspLogin();
        Page<PostpayOrderVo> page = new Page<>(req.getPageNo(), req.getPageSize());
        String customerTable="csp_customer_"+cspId;
        page = baseMapper.searchPostpayOrder(req.getPaymentDays(), req.getEnterpriseAccountName(), req.getStatus(), cspId,customerTable, page);
        List<PostpayOrderVo> records = page.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            //填充手机号
            List<String> customerIdList = records.stream()
                    .map(PostpayOrderVo::getCustomerId)
                    .collect(Collectors.toList());
            Map<String, String> userPhoneMap = cspCustomerApi.getByCustomerIds(customerIdList)
                    .stream()
                    .collect(Collectors.toMap(UserInfoVo::getCustomerId, UserInfoVo::getPhone));
            for (PostpayOrderVo record : records) {
                record.setPhone(userPhoneMap.get(record.getCustomerId()));
            }
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 备注订单，每次覆盖备注（可传空字符串）
     *
     * @param noteVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void noteOrder(PostpayOrderNoteVo noteVo) {
        boolean updated = this.lambdaUpdate()
                .eq(PostpayOrder::getId, noteVo.getId())
                .likeRight(PostpayOrder::getCustomerId, SessionContextUtil.getLoginUser().getUserId())
                .set(PostpayOrder::getNote, noteVo.getNote())
                .update(new PostpayOrder());
        if (!updated)
            throw new BizException("备注失败");
    }

    /**
     * 支付完成订单
     *
     * @param finishVo
     */
    @Transactional(rollbackFor = Exception.class)
    public void finishPay(PostpayOrderFinishVo finishVo) {
       PostpayOrder postpayOrder= this.getBaseMapper().selectById(finishVo.getId());
       String cspId=SessionContextUtil.getUser().getCspId();
       if(!StrUtil.contains(postpayOrder.getCustomerId(),cspId))
       {
           log.warn("修改他人后付费订单：{}，user:{}", JSON.toJSONString(finishVo),JSON.toJSONString(SessionContextUtil.getUser()));
           throw new BizException("越权操作");
       }
        String serialNumber = finishVo.getSerialNumber();
        SerialNumberUtils.applyGlobalUniqueSerialNumber(SerialNumberUtils.MSG_ORDER, serialNumber);
        try {
            finishVo.setPayAmount(Double.valueOf(finishVo.getPayAmount()).toString());
        }catch (Exception exception)
        {
            throw new BizException("支付金额非法");
        }
        boolean updated = this.lambdaUpdate()
                .eq(PostpayOrder::getId, finishVo.getId())
                .eq(PostpayOrder::getStatus, PostpayOrderStatus.WAITING)
                .likeRight(PostpayOrder::getCustomerId, SessionContextUtil.getLoginUser().getUserId())
                .set(PostpayOrder::getSerialNumber, serialNumber)
                .set(PostpayOrder::getPayAmount, finishVo.getPayAmount())
                .set(PostpayOrder::getPayTime, LocalDateTime.now())
                .set(PostpayOrder::getStatus, PostpayOrderStatus.FINISH)
                .update(new PostpayOrder());
        if (!updated)
            throw new BizException("支付完成失败");
    }

    /**
     * 客户搜索后付费订单
     */
    public PageResult<PostpayOrderCustomerVo> customerSearchPostpayOrder(PostpayOrderQueryVo req) {
        BaseUser user = SessionContextUtil.getLoginUser();
        if (!user.getIsCustomer())
            throw new BizException("权限异常");
        Page<PostpayOrderCustomerVo> page = new Page<>(req.getPageNo(), req.getPageSize());
        page = baseMapper.customerSearchPostpayOrder(req.getPaymentDays(), req.getStatus(), user.getUserId(), req.getStartTime(), req.getEndTime(), page);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    public PageResult<InvoiceOrderPostPageInfo> postPageSelect(InvoiceOrderPostQuery query) {
        LambdaQueryWrapper<PostpayOrder> ne = new LambdaQueryWrapper<PostpayOrder>()
                .eq(PostpayOrder::getCustomerId, query.getCustomerId())
                .eq(PostpayOrder::getInvoicing, 0)
                .gt(PostpayOrder::getInvoicableAmount, 0)
                .ne(PostpayOrder::getStatus, PostpayOrderStatus.EMPTY_ORDER)
                .like(PostpayOrder::getPaymentDays, query.getPaymentDays())
                .ge(Objects.nonNull(query.getStartTime()), PostpayOrder::getPayTime, DateUtil.getBeginDay(query.getStartTime()))
                .lt(Objects.nonNull(query.getEndTime()), PostpayOrder::getPayTime, DateUtil.getBeginAfterDay(query.getEndTime()))
                .orderByDesc(PostpayOrder::getCreateTime);

        Page<PostpayOrder> page = new Page<>(query.getPageNo(), query.getPageSize());
        page(page, ne);
        List<InvoiceOrderPostPageInfo> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            list = page.getRecords().stream().map(s -> {
                InvoiceOrderPostPageInfo info = new InvoiceOrderPostPageInfo();
                BeanUtils.copyProperties(s, info);
                return info;
            }).collect(Collectors.toList());
        }
        return new PageResult<>(list, page.getTotal());
    }

    public BigDecimal postInvoicableAmount(String cusId) {
        BigDecimal bigDecimal = getBaseMapper().sumPostInvoicableAmount(cusId);
        if (bigDecimal == null) return BigDecimal.ZERO;
        return bigDecimal;
    }

    public Boolean existsMsgRecordByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
        return msgRecordApi.existsMsgRecordByMsgTypeAndAccountId(customerId, msgType, accountId);
    }
}
