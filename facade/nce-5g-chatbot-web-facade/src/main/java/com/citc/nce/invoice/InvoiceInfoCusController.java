package com.citc.nce.invoice;

import com.citc.nce.auth.invoice.api.InvoiceInfoCspApi;
import com.citc.nce.auth.invoice.api.InvoiceInfoCustomerApi;
import com.citc.nce.auth.invoice.domain.ElectronInvoice;
import com.citc.nce.auth.invoice.domain.PaperInvoice;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCustomerVo;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 2024/2/27
 */

@Api(tags = "v14 发票信息管理-客户")
@RestController
@RequestMapping("/invoiceInfoCustomer")
@AllArgsConstructor
public class InvoiceInfoCusController {


    private InvoiceInfoCustomerApi customerApi;
    private InvoiceInfoCspApi cspApi;

    @GetMapping("/invoice/info/cspCapacity")
    @ApiOperation("查询我的csp发票配置")
    public InvoiceInfoCspVo getByCspId() {
        return cspApi.getByVoCspId(SessionContextUtil.getLoginUser().getCspId());
    }

    @GetMapping("/invoice/info/customer")
    @ApiOperation("查询我的发票配置")
    public InvoiceInfoCustomerVo getByCustomerVoId() {
        return customerApi.getByCustomerVoId(SessionContextUtil.getLoginUser().getUserId());
    }

    @PostMapping("/invoice/info/customer/update/paper")
    @ApiOperation("修改我的纸质发票配置")
    public void updateByCustomerId(@RequestBody @Valid PaperInvoice update) {
        customerApi.updatePaperInvoiceByCustomerId(update);
    }

    @PostMapping("/invoice/info/customer/update/electron")
    @ApiOperation("修改我的电子发票配置")
    public void updateByCustomerId(@RequestBody @Valid ElectronInvoice update) {
        customerApi.updateElectronInvoiceByCustomerId(update);
    }
}
