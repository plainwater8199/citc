package com.citc.nce.auth.invoice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.dao.InvoiceInfoCustomerMapper;
import com.citc.nce.auth.invoice.domain.Invoice;
import com.citc.nce.auth.invoice.domain.InvoiceInfoCustomer;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.citc.nce.auth.invoice.service.IInvoiceInfoCustomerService;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 发票信息管理-客户 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:57
 */
@Service
public class InvoiceInfoCustomerServiceImpl extends ServiceImpl<InvoiceInfoCustomerMapper, InvoiceInfoCustomer> implements IInvoiceInfoCustomerService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public InvoiceInfoCustomer getByCustomerId(String customerId) {
        return lambdaQuery().eq(InvoiceInfoCustomer::getCustomerId, customerId).one();
    }

    @Override
    public InvoiceInfoCustomerVo getByCustomerVoId(String customerId) {
        InvoiceInfoCustomer customer = getByCustomerId(customerId);
        if (Objects.isNull(customer)) return null;
        InvoiceInfoCustomerVo vo = new InvoiceInfoCustomerVo();
        BeanUtils.copyProperties(customer, vo);
        try {
            Invoice invoice = objectMapper.readValue(customer.getJsonBody(), Invoice.class);
            vo.setType(invoice.getInvoiceType().getValue());
        } catch (JacksonException jacksonException) {
            log.error("JacksonException {}", jacksonException);
            throw new BizException("发票信息解析实在");
        }
        return vo;
    }

    @Override
    @Transactional
    public void updateByCustomerId(Invoice update) {
        String customerId = SessionContextUtil.getLoginUser().getUserId();
        checkInvoiceInfo(update);
        InvoiceInfoCustomer infoCustomer = getByCustomerId(customerId);
        if (Objects.isNull(infoCustomer)) {
            infoCustomer = new InvoiceInfoCustomer();
            infoCustomer.setCustomerId(customerId);
        }
        infoCustomer.setHeaderType(update.getHeaderType());
        infoCustomer.setInvoiceType(update.getInvoiceType());
        infoCustomer.setJsonBody(JSON.toJSONString(update));
        saveOrUpdate(infoCustomer);
    }

    private void checkInvoiceInfo(Invoice update) {
        InvoiceType invoiceType = update.getInvoiceType();
        List<InvoiceType>  specialList = Arrays.asList(InvoiceType.VAT_SPECIAL_ELECTRONIC,InvoiceType.VAT_SPECIAL_PAPER);
        if(specialList.contains(invoiceType)){
            if(!StringUtils.hasLength(update.getVatNum())){
                throw new BizException("增值税税号不能为空");
            }
            if(!StringUtils.hasLength(update.getPhone())){
                throw new BizException("电话不能为空");
            }
            if(!StringUtils.hasLength(update.getBank())){
                throw new BizException("开户行名称不能为空");
            }
            if(!StringUtils.hasLength(update.getBankAccountNum())){
                throw new BizException("开户行账号不能为空");
            }
        }

        if("enterprise".equals(update.getHeaderType())){
            if(!StringUtils.hasLength(update.getVatNum())){
                throw new BizException("税号不能为空");
            }
        }


    }
}
