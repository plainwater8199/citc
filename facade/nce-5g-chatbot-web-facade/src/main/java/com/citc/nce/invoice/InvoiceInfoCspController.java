package com.citc.nce.invoice;

import com.citc.nce.auth.invoice.api.InvoiceInfoCspApi;
import com.citc.nce.auth.invoice.vo.CspUpdateInvoiceInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;
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
@Api(tags = "v14 发票信息管理-csp")
@RestController
@RequestMapping("/invoiceInfoCsp")
@AllArgsConstructor
public class InvoiceInfoCspController {

    private InvoiceInfoCspApi invoiceInfoCspApi;


    @GetMapping("/invoice/info/csp/my")
    @ApiOperation("csp 查询自己设置的发票信息")
    public InvoiceInfoCspVo getByCspId() {
        return invoiceInfoCspApi.getByVoCspId(SessionContextUtil.getLoginUser().getCspId());
    }

    @PostMapping("/invoice/info/csp/update")
    @ApiOperation("csp 修改自己设置的发票信息")
    public void updateById(@RequestBody @Valid CspUpdateInvoiceInfo update) {
        update.setCspId(SessionContextUtil.getLoginUser().getCspId());
        invoiceInfoCspApi.updateById(update);
    }

}
