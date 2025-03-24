package com.citc.nce.auth.invoice.service;

import com.citc.nce.auth.invoice.domain.InvoiceInfoCsp;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.invoice.vo.CspUpdateInvoiceInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;

/**
 * <p>
 * 发票信息管理-csp 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
public interface IInvoiceInfoCspService extends IService<InvoiceInfoCsp> {

    InvoiceInfoCspVo getByVoCspId(String cspId);

    InvoiceInfoCsp getByCspId(String cspId);

    void update(CspUpdateInvoiceInfo update);
}
