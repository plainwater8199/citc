package com.citc.nce.auth.invoice.api;

/*
 *
 * @author bydud
 * @since 2024/2/27
 */

import com.citc.nce.auth.invoice.vo.CspUpdateInvoiceInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "auth-service", contextId = "invoiceInfoCsp-api", url = "${auth:}")
public interface InvoiceInfoCspApi {
    @GetMapping("/invoice/info/csp/{cspId}")
    InvoiceInfoCspVo getByVoCspId(@PathVariable("cspId") String cspId);

    @PostMapping("/invoice/info/csp/update")
    void updateById(@RequestBody @Valid CspUpdateInvoiceInfo update);
}
