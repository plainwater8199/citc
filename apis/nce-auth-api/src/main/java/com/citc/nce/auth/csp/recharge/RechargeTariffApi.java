package com.citc.nce.auth.csp.recharge;

import com.citc.nce.auth.csp.recharge.vo.RechargeTariffAdd;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffExistReq;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffOptionsReq;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "auth-service", contextId = "rechargeTariffApi", url = "${auth:}")
public interface RechargeTariffApi {
    @PostMapping("/recharge/tariff/add")
    void addRechargeTariff(@RequestBody @Valid RechargeTariffAdd req);

    @GetMapping("/recharge/{accountId}/tariff")
    RechargeTariffDetailResp getRechargeTariff(@PathVariable("accountId") String accountId);

    @PostMapping("/recharge/chatbot/tariff/exist")
    RechargeTariffDetailResp checkRechargeTariffExist(@RequestBody @Valid RechargeTariffExistReq req);

    @PostMapping("/recharge/tariff/options")
    TariffOptions showRechargeTariffOptions(@RequestBody @Valid RechargeTariffOptionsReq req);
}
