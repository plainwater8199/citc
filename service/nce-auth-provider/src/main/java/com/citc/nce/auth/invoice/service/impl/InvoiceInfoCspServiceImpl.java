package com.citc.nce.auth.invoice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.invoice.dao.InvoiceInfoCspMapper;
import com.citc.nce.auth.invoice.domain.InvoiceInfoCsp;
import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.citc.nce.auth.invoice.service.IInvoiceInfoCspService;
import com.citc.nce.auth.invoice.vo.CspUpdateInvoiceInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 发票信息管理-csp 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
@Service
public class InvoiceInfoCspServiceImpl extends ServiceImpl<InvoiceInfoCspMapper, InvoiceInfoCsp> implements IInvoiceInfoCspService {

    @Override
    public InvoiceInfoCspVo getByVoCspId(String cspId) {
        InvoiceInfoCsp infoCsp = getByCspId(cspId);
        if (Objects.isNull(infoCsp)) return null;
        InvoiceInfoCspVo vo = new InvoiceInfoCspVo();
        BeanUtils.copyProperties(infoCsp, vo);
        vo.setTypeList(getTypeList(infoCsp.getTypeList()));
        return vo;
    }

    @Override
    public InvoiceInfoCsp getByCspId(String cspId) {
        return this.lambdaQuery().eq(InvoiceInfoCsp::getCspId, cspId).one();
    }

    @Override
    @Transactional
    public void update(CspUpdateInvoiceInfo update) {
        InvoiceInfoCsp infoCsp = getByCspId(update.getCspId());
        if (Objects.isNull(infoCsp)) {
            infoCsp = new InvoiceInfoCsp();
        }
        infoCsp.setCspId(update.getCspId());
        infoCsp.setRemark(update.getRemark());
        infoCsp.setTypeList(getTypeListSetString(update.getTypeList()));
        saveOrUpdate(infoCsp);
    }


    public String getTypeListSetString(List<InvoiceType> typeList) {
        Set<String> set = typeList.stream().map(InvoiceType::getValue).collect(Collectors.toSet());
        return String.join(";", set);
    }

    public List<InvoiceType> getTypeList(String typeList) {
        if (StringUtils.hasText(typeList)) {
            String[] split = typeList.split(";");
            List<InvoiceType> list = new ArrayList<>(split.length);
            for (String string : split) {
                list.add(InvoiceType.valueOf(string));
            }
            return list;
        }
        return new ArrayList<>();
    }
}
