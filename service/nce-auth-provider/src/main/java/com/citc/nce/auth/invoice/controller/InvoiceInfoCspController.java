package com.citc.nce.auth.invoice.controller;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.citc.nce.auth.invoice.api.InvoiceInfoCspApi;
import com.citc.nce.auth.invoice.service.IInvoiceInfoCspService;
import com.citc.nce.auth.invoice.vo.CspUpdateInvoiceInfo;
import com.citc.nce.auth.invoice.vo.InvoiceInfoCspVo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 发票信息管理-csp 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-02-27 10:02:50
 */
@RestController
@AllArgsConstructor
public class InvoiceInfoCspController implements InvoiceInfoCspApi {

    private final IInvoiceInfoCspService infoCspService;


    @Override
    public InvoiceInfoCspVo getByVoCspId(String cspId) {
        return infoCspService.getByVoCspId(cspId);
    }

    @Override
    public void updateById(CspUpdateInvoiceInfo update) {
        Assert.notEmpty(update.getCspId(), "cspId不能为空");
        infoCspService.update(update);
    }
}

