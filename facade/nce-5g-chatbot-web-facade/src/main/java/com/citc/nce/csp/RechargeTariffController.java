package com.citc.nce.csp;

import com.citc.nce.auth.csp.chatbot.ChatbotApi;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotTariffAdd;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotTariffDetailResp;
import com.citc.nce.auth.csp.customer.vo.CustomerGetDetailReq;
import com.citc.nce.auth.csp.recharge.RechargeTariffApi;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffAdd;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffOptionsReq;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.authcenter.csp.vo.CustomerDetailReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.CustomerOptionReq;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(value = "RechargeTariffController", tags = "资费接口")
public class RechargeTariffController {
    @Resource
    private RechargeTariffApi rechargeTariffApi;

    @PostMapping("/recharge/tariff/add")
    @ApiOperation("新增资费")
    @HasCsp
    public void addRechargeTariff(@RequestBody @Valid RechargeTariffAdd req) {
        rechargeTariffApi.addRechargeTariff(req);
    };

    @GetMapping("/recharge/{accountId}/tariff")
    @ApiOperation("查询资费")
    public RechargeTariffDetailResp getRechargeTariff(@PathVariable("accountId") String accountId) {
        return rechargeTariffApi.getRechargeTariff(accountId);
    };

    @PostMapping("/recharge/tariff/options")
    @ApiOperation("展示资费配置")
    @HasCsp
    public TariffOptions showRechargeTariffOptions(@RequestBody @Valid RechargeTariffOptionsReq req) {
        return rechargeTariffApi.showRechargeTariffOptions(req);
    };
}
