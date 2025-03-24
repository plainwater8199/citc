package com.citc.nce.auth.invoice.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.recharge.Const.PrepaymentStatus;
import com.citc.nce.auth.invoice.dao.InvoiceManageMapper;
import com.citc.nce.auth.invoice.domain.Invoice;
import com.citc.nce.auth.invoice.domain.InvoiceManage;
import com.citc.nce.auth.invoice.domain.InvoiceManageFile;
import com.citc.nce.auth.invoice.domain.InvoiceManageOrder;
import com.citc.nce.auth.invoice.enums.InvoiceStatus;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.citc.nce.auth.invoice.service.IInvoiceInfoCustomerService;
import com.citc.nce.auth.invoice.service.IInvoiceManageFileService;
import com.citc.nce.auth.invoice.service.IInvoiceManageOpLogService;
import com.citc.nce.auth.invoice.service.IInvoiceManageOrderService;
import com.citc.nce.auth.invoice.service.IInvoiceManageService;
import com.citc.nce.auth.invoice.service.InvoiceAccountService;
import com.citc.nce.auth.invoice.vo.InvoiceApply;
import com.citc.nce.auth.invoice.vo.InvoiceExportInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoVo;
import com.citc.nce.auth.invoice.vo.InvoiceManageAudit;
import com.citc.nce.auth.invoice.vo.InvoicePageInfo;
import com.citc.nce.auth.invoice.vo.InvoicePageInfoCsp;
import com.citc.nce.auth.invoice.vo.InvoicePageQuery;
import com.citc.nce.auth.invoice.vo.InvoicePostOrder;
import com.citc.nce.auth.invoice.vo.InvoicePostOrderDetail;
import com.citc.nce.auth.invoice.vo.InvoicePrepayOrder;
import com.citc.nce.auth.invoice.vo.UploadInvoice;
import com.citc.nce.auth.postpay.order.entity.PostpayOrder;
import com.citc.nce.auth.postpay.order.entity.PostpayOrderDetail;
import com.citc.nce.auth.postpay.order.service.PostpayOrderDetailService;
import com.citc.nce.auth.postpay.order.service.PostpayOrderService;
import com.citc.nce.auth.prepayment.entity.PrepaymentOrder;
import com.citc.nce.auth.prepayment.service.IPrepaymentOrderService;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 发票申请、开具管理 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-02-28 02:02:25
 */
@Service
public class InvoiceManageServiceImpl extends ServiceImpl<InvoiceManageMapper, InvoiceManage> implements IInvoiceManageService {

    @Autowired
    private IPrepaymentOrderService preOrderService;
    @Autowired
    private PostpayOrderService postpayOrderService;
    @Autowired
    private PostpayOrderDetailService postDetailService;

    @Autowired
    private IInvoiceInfoCustomerService invoiceCustomerService;

    @Autowired
    private IInvoiceManageOrderService manageOrderService;

    @Autowired
    private IInvoiceManageFileService invoiceFileService;

    @Autowired
    private CspCustomerApi customerApi;

    @Autowired
    private InvoiceAccountService accountService;

    @Autowired
    private IInvoiceManageOpLogService opLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void apply(InvoiceApply apply) {
        //查询开票信息
        InvoiceInfoCustomerVo invoiceInfo = invoiceCustomerService.getByCustomerVoId(apply.getCreate());
        if (Objects.isNull(invoiceInfo)) {
            throw new BizException("开票信息未设置不允许开票");
        }

        InvoiceManageServiceImpl bean = SpringUtil.getBean(this.getClass());

        if (prepaymentOrder.equals(apply.getType())) {
            GetOrderList<PrepaymentOrder> result = getGetPrepaymentOrder(apply, apply.getCreate());
            bean.apply(invoiceInfo, apply.getType(), result.orderAmount, result.invoicableAmount, result.orderList, null);
        } else {
            GetOrderList<PostpayOrder> result = getGetPostpaymentOrder(apply, apply.getCreate());
            bean.apply(invoiceInfo, apply.getType(), result.orderAmount, result.invoicableAmount, null, result.orderList);
        }

    }


    @Override
    public Page<InvoiceManage> pageInfo(InvoicePageQuery query) {
        Collection<String> userIds = null;
        if (StringUtils.hasLength(query.getEnterpriseAccountName())) {
            Collection<UserEnterpriseInfoVo> name = customerApi.getUserUserIdsLikeEnterpriseAccountName(query.getEnterpriseAccountName());
            if (CollectionUtils.isEmpty(name)) {
                Page<InvoiceManage> page = new Page<>(query.getPageNo(), query.getPageSize());
                page.setTotal(0);
                return page;
            }
            userIds = name.stream().map(UserEnterpriseInfoVo::getUserId).collect(Collectors.toSet());
        }

        LambdaQueryWrapper<InvoiceManage> queryWrapper = new LambdaQueryWrapper<InvoiceManage>()
                .eq(StringUtils.hasLength(query.getCspId()), InvoiceManage::getCspId, query.getCspId())
                .eq(StringUtils.hasLength(query.getCreator()), InvoiceManage::getCreator, query.getCreator())
                .eq(Objects.nonNull(query.getStatus()), InvoiceManage::getStatus, query.getStatus())
                .eq(Objects.nonNull(query.getInvoiceType()), InvoiceManage::getInvoiceType, query.getInvoiceType())
                .in(!CollectionUtils.isEmpty(userIds), InvoiceManage::getCreator, userIds)
                .ge(Objects.nonNull(query.getStartTime()), InvoiceManage::getCreateTime, query.getStartTime())
                .lt(Objects.nonNull(query.getEndTime()), InvoiceManage::getCreateTime, query.getEndTime())
                .orderByDesc(InvoiceManage::getCreateTime);
        Page<InvoiceManage> page = new Page<>(query.getPageNo(), query.getPageSize());
        page(page, queryWrapper);
        return page;
    }

    @Override
    public List<InvoicePageInfo> convertInvoicePageInfo(List<InvoiceManage> records) {
        return records.stream().map(manage -> {
            InvoicePageInfo info = new InvoicePageInfo();
            BeanUtils.copyProperties(manage, info);
            String invoiceInfoJson = manage.getInvoiceInfoJson();
            if (StringUtils.hasLength(invoiceInfoJson)) {
                InvoiceInfoVo invoiceInfo = JSON.parseObject(invoiceInfoJson, InvoiceInfoVo.class);
                try {
                    Invoice invoice = objectMapper.readValue(invoiceInfo.getJsonBody(), Invoice.class);
                    info.setHeaderType(invoice.getHeaderType());
                    info.setHeaderStr(invoice.getHeaderStr());
                } catch (JsonProcessingException e) {
                    log.error("JsonProcessingException", e);
                }
                info.setInvoiceValue(invoiceInfo.getInvoiceValue());
            }
            //已拒绝的查询原因
            if (InvoiceStatus.rejected.equals(info.getStatus())) {
                info.setStatusRemake(opLogService.getLastRemake(info.getImId(), info.getStatus()));
            }
            return info;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InvoicePageInfoCsp> convertInvoicePageInfoCsp(List<InvoiceManage> records) {
        if (CollectionUtils.isEmpty(records)) return new ArrayList<>(2);
        Set<String> customer = records.stream().map(InvoiceManage::getCreator).collect(Collectors.toSet());
        Map<String, String> userMap = customerApi.getUserEnterpriseInfoByUserIds(customer).stream()
                .collect(Collectors.toMap(UserEnterpriseInfoVo::getUserId, UserEnterpriseInfoVo::getEnterpriseAccountName));

        return records.stream().map(manage -> {
            InvoicePageInfoCsp info = new InvoicePageInfoCsp();
            BeanUtils.copyProperties(manage, info);
            String invoiceInfoJson = manage.getInvoiceInfoJson();
            if (StringUtils.hasLength(invoiceInfoJson)) {
                InvoiceInfoVo invoiceInfo = JSON.parseObject(invoiceInfoJson, InvoiceInfoVo.class);
                info.setInvoiceInfoVo(invoiceInfo);
            }
            info.setEnterpriseAccountName(userMap.get(manage.getCreator()));
            // 已开票查询发票信息
            if (InvoiceStatus.invoiced.equals(info.getStatus())) {
                info.setInvoiceFile(getInvoiceFileByImId(manage.getImId()));
            }
            return info;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InvoicePrepayOrder> getPrepayInvoiceOrderDetail(Long imId) {
        InvoiceManage manage = getById(imId);
        if (Objects.isNull(manage)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        List<InvoiceManageOrder> list = manageOrderService.listByImId(imId);
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>();
        //当前订单数据（需求这么要求的，我也不知到为什么要这样）
        List<PrepaymentOrder> orderList = preOrderService.listByIds(list.stream().map(InvoiceManageOrder::getOrderId).collect(Collectors.toList()));
        Map<String, String> map = new HashMap<>();
        return orderList.stream()
                .sorted(Comparator.comparing(PrepaymentOrder::getCreateTime).reversed())
                .map(orderSh -> {
                    InvoicePrepayOrder v = new InvoicePrepayOrder();
                    BeanUtils.copyProperties(orderSh, v);
                    v.setImId(imId);
                    v.setCspId(manage.getCspId());
                    v.setMsgType(orderSh.getMsgType().getDesc());
                    v.setAccountName(accountService.getAccountName(map, orderSh.getAccountId(), orderSh.getMsgType()));
                    return v;
                }).collect(Collectors.toList());
    }

    @Override
    public List<InvoicePostOrder> getPostPayInvoiceOrderDetail(Long imId) {
        InvoiceManage manage = getById(imId);
        if (Objects.isNull(manage)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        List<InvoiceManageOrder> list = manageOrderService.listByImId(imId);
        if (CollectionUtils.isEmpty(list)) return new ArrayList<>();
        //当前订单数据（需求这么要求的，我也不知到为什么要这样）
        List<PostpayOrder> orderList = postpayOrderService.listByIds(list.stream().map(InvoiceManageOrder::getOrderId).collect(Collectors.toList()));
        return orderList.stream()
                .sorted(Comparator.comparing(PostpayOrder::getCreateTime).reversed())
                .map(orderSh -> {
                    InvoicePostOrder v = new InvoicePostOrder();
                    BeanUtils.copyProperties(orderSh, v);
                    v.setImId(imId);
                    v.setCspId(manage.getCspId());
                    List<PostpayOrderDetail> details = postDetailService.listByOrderId(orderSh.getOrderId());
                    v.setOrderDetails(convertToInvoicePostOrderDetail(details));
                    return v;
                }).collect(Collectors.toList());
    }

    private List<InvoicePostOrderDetail> convertToInvoicePostOrderDetail(List<PostpayOrderDetail> details) {
        if (CollectionUtils.isEmpty(details)) new ArrayList<>();
        HashMap<String, String> map = new HashMap<>();
        return details.stream().map(s -> {
            InvoicePostOrderDetail o = new InvoicePostOrderDetail();
            BeanUtils.copyProperties(s, o);
            o.setAccountName(accountService.getAccountName(map, o.getAccountId(), o.getMsgType()));
            return o;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void applyCancel(Long imId) {
        InvoiceManage manage = getById(imId);
        if (Objects.isNull(manage)) return;
        if (!InvoiceStatus.audit.equals(manage.getStatus())) {
            throw new BizException(String.format("当前状态(%s)不允许取消", manage.getStatus().getAlias()));
        }
        SessionContextUtil.sameCus(manage.getCreator());
        manage.setStatus(InvoiceStatus.canceled);
        manage.setUpdater(null);
        manage.setUpdateTime(null);
        updateById(manage);
        manageOrderService.fallbackInvoicingStatus(imId);
        opLogService.saveLog(imId, InvoiceStatus.canceled, null);
    }

    @Override
    @Transactional
    public void audit(InvoiceManageAudit audit) {
        InvoiceManage manage = getById(audit.getImId());
        if (Objects.isNull(manage)) return;
        SessionContextUtil.sameCsp(manage.getCspId());
        if (!InvoiceStatus.audit.equals(manage.getStatus())) {
            throw new BizException(String.format("当前状态(%s)不允许审核", manage.getStatus().getAlias()));
        }

        InvoiceStatus status = audit.isPass() ? InvoiceStatus.invoicing : InvoiceStatus.rejected;
        manage.setStatus(status);
        manage.setUpdater(null);
        manage.setUpdateTime(null);
        updateById(manage);
        opLogService.saveLog(audit.getImId(), status, audit.getRemark());
        if (InvoiceStatus.rejected.equals(status)) {
            //拒绝了回退订单内的开票状态
            manageOrderService.fallbackInvoicingStatus(manage.getImId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void completeInvoiced(UploadInvoice invoice) {
        InvoiceManage manage = getById(invoice.getImId());
        if (Objects.isNull(manage)) return;
        SessionContextUtil.sameCsp(manage.getCspId());
        manage.setStatus(InvoiceStatus.invoiced);
        updateById(manage);
        if (Objects.nonNull(invoice.getInvoiceFile())) {
            InvoiceManageFile file = invoice.getInvoiceFile();
            file.setImId(invoice.getImId());
            invoiceFileService.save(file);
        }
        opLogService.saveLog(invoice.getImId(), InvoiceStatus.invoiced, null);
    }

    @Override
    public InvoiceManageFile getInvoiceFileByImId(Long imId) {
        InvoiceManage manage = getById(imId);
        if (Objects.isNull(manage)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }

        List<InvoiceManageFile> list = invoiceFileService.listByImId(imId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        InvoiceManageFile file = list.get(0);
        file.setCspId(manage.getCspId());
        return file;
    }

    @Override
    public List<InvoiceExportInfo> getExportInvoiceExportInfoImByIds(List<Long> imIds) {
        List<InvoiceManage> list = listByIds(imIds);
        if (CollectionUtils.isEmpty(list)) {
            throw new BizException("没有可导出数据");
        }
        //检查数据都是当前csp的
        list.stream().map(InvoiceManage::getCspId).forEach(SessionContextUtil::sameCsp);
        List<InvoicePageInfoCsp> cspList = convertInvoicePageInfoCsp(list);
        DecimalFormat df = new DecimalFormat("#0.00");
        return cspList.stream().map(s -> {
            InvoiceInfoVo invoiceInfo = s.getInvoiceInfoVo();
            InvoiceExportInfo excel = new InvoiceExportInfo();
            excel.setEnterpriseAccountName(s.getEnterpriseAccountName());
            excel.setInvoiceValue(df.format(invoiceInfo.getInvoiceValue()));
            try {
                Invoice jsonBody = objectMapper.readValue(invoiceInfo.getJsonBody(), Invoice.class);
                BeanUtils.copyProperties(jsonBody, excel);
                excel.setType(jsonBody.getInvoiceType().getAlias());
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException", e);
            }
            return excel;
        }).collect(Collectors.toList());
    }

    @NotNull
    private GetOrderList<PrepaymentOrder> getGetPrepaymentOrder(InvoiceApply apply, String userId) {
        List<PrepaymentOrder> orderList = preOrderService.listByIds(apply.getOrderIds());

        //查询出的订单数小于传入的
        if (CollectionUtils.isEmpty(orderList) || orderList.stream()
                .filter(s -> s.getCustomerId().equals(userId)).count() < apply.getOrderIds().size()) {
            throw new BizException("订单不存在");
        }
        if (orderList.stream().anyMatch(s -> PrepaymentStatus.CANCELED.equals(s.getStatus()))) {
            throw new BizException("已取消的订单,不能开票");
        }
        //不能选择已开票的
        if (orderList.stream().anyMatch(s -> Boolean.TRUE.equals(s.getInvoicing()))) {
            throw new BizException("已开票订单,不能开票");
        }

        BigDecimal orderAmount = orderList.stream().map(order -> new BigDecimal(order.getAmount())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal invoicableAmount = orderList.stream().map(PrepaymentOrder::getInvoicableAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return new GetOrderList<>(orderList, orderAmount, invoicableAmount);
    }

    private GetOrderList<PostpayOrder> getGetPostpaymentOrder(InvoiceApply apply, String userId) {
        List<PostpayOrder> orderList = postpayOrderService.listByIds(apply.getOrderIds());

        //查询出的订单数小于传入的
        if (CollectionUtils.isEmpty(orderList) || orderList.stream()
                .filter(s -> s.getCustomerId().equals(userId)).count() < apply.getOrderIds().size()) {
            throw new BizException("订单不存在");
        }
        //不能选择已开票的
        if (orderList.stream().anyMatch(s -> Boolean.TRUE.equals(s.getInvoicing()))) {
            throw new BizException("已开票订单,不能开票");
        }

        BigDecimal orderAmount = orderList.stream().map(order -> new BigDecimal(order.getAmount())).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal invoicableAmount = orderList.stream().map(PostpayOrder::getInvoicableAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return new GetOrderList<>(orderList, orderAmount, invoicableAmount);
    }

    private static class GetOrderList<E> {
        public final List<E> orderList;
        public final BigDecimal orderAmount;
        public final BigDecimal invoicableAmount;

        public GetOrderList(List<E> orderList, BigDecimal orderAmount, BigDecimal invoicableAmount) {
            this.orderList = orderList;
            this.orderAmount = orderAmount;
            this.invoicableAmount = invoicableAmount;
        }
    }

    /**
     * 保存开票对象
     *
     * @param invoiceInfo         开票信息
     * @param type                开票类型
     * @param orderAmount         订单总金额
     * @param invoicableAmount    可开票金额
     * @param prepaymentOrderList 预付费订单
     * @param postOrderList       后费订单
     */
    @Transactional
    public void apply(InvoiceInfoCustomerVo invoiceInfo, String type, BigDecimal orderAmount, BigDecimal invoicableAmount,
                      List<PrepaymentOrder> prepaymentOrderList, List<PostpayOrder> postOrderList) {
        InvoiceManage manage = new InvoiceManage();
        manage.setType(type);//付费方式
        manage.setOrderAmount(orderAmount);
        manage.setInvoicableAmount(invoicableAmount);
        //发票信息
        try {
            InvoiceInfoVo invoiceInfoVo = new InvoiceInfoVo();
            BeanUtils.copyProperties(invoiceInfo, invoiceInfoVo);
            invoiceInfoVo.setInvoiceValue(invoicableAmount);
            manage.setInvoiceInfoJson(objectMapper.writeValueAsString(invoiceInfoVo));
        } catch (JacksonException jacksonException) {
            log.error("JacksonException {}", jacksonException);
            throw new BizException("发票信息解析失败");
        }
        manage.setStatus(InvoiceStatus.audit);
        manage.setInvoiceType(InvoiceType.byValue(invoiceInfo.getType()));
        //保存发票申请
        save(manage);
        //保存订单数数据
        if (!CollectionUtils.isEmpty(prepaymentOrderList)) {
            manageOrderService.saveBatchPre(type, manage.getImId(), prepaymentOrderList);
        }
        if (!CollectionUtils.isEmpty(postOrderList)) {
            manageOrderService.saveBatchPost(type, manage.getImId(), postOrderList);
        }
    }
}
