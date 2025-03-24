package com.citc.nce.auth.invoice.controller;


import com.citc.nce.auth.invoice.api.InvoiceInfoCustomerApi;
import com.citc.nce.auth.invoice.domain.PaperInvoice;
import com.citc.nce.auth.invoice.domain.ElectronInvoice;
import com.citc.nce.auth.invoice.service.IInvoiceInfoCustomerService;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 发票信息管理-客户 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:57
 */
@RestController
public class InvoiceInfoCustomerController implements InvoiceInfoCustomerApi {

    @Autowired
    private IInvoiceInfoCustomerService infoCustomerService;

    @Override
    public InvoiceInfoCustomerVo getByCustomerVoId(String customerId) {
        return infoCustomerService.getByCustomerVoId(customerId);
    }

    @Override
    public void updatePaperInvoiceByCustomerId(PaperInvoice update) {
        infoCustomerService.updateByCustomerId(update);
    }

    @Override
    public void updateElectronInvoiceByCustomerId(ElectronInvoice update) {
        infoCustomerService.updateByCustomerId(update);
    }

}

