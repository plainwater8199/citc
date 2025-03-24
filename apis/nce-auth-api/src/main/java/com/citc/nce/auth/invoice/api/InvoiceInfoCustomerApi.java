package com.citc.nce.auth.invoice.api;

/*
 *
 * @author bydud
 * @since 2024/2/27
 */

import com.citc.nce.auth.invoice.domain.PaperInvoice;
import com.citc.nce.auth.invoice.domain.ElectronInvoice;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "auth-service", contextId = "invoiceInfoCustomer-api", url = "${auth:}")
public interface InvoiceInfoCustomerApi {
    @GetMapping("/invoice/info/customer/{customerId}")
    InvoiceInfoCustomerVo getByCustomerVoId(@PathVariable("customerId") String customerId);

    @PostMapping("/invoice/info/customer/update/paper")
    void updatePaperInvoiceByCustomerId(@RequestBody @Valid PaperInvoice update);

    @PostMapping("/invoice/info/customer/update/electron")
    void updateElectronInvoiceByCustomerId(@RequestBody @Valid ElectronInvoice update);
}
