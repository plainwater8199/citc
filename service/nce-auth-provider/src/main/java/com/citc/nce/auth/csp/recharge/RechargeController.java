package com.citc.nce.auth.csp.recharge;

import com.citc.nce.auth.csp.recharge.service.ChargeTariffService;
import com.citc.nce.auth.csp.recharge.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yy
 * @date 2024-10-16 17:30:45
 */
@RestController
@Slf4j
public class RechargeController implements RechargeTariffApi {

    @Autowired
    private ChargeTariffService chargeTariffService;
    @Override
    public void addRechargeTariff(RechargeTariffAdd req) {
        chargeTariffService.addRechargeTariff(req);
    }

    @Override
    public RechargeTariffDetailResp getRechargeTariff(String accountId) {
        return chargeTariffService.getRechargeTariff(accountId);
    }

    @Override
    public TariffOptions showRechargeTariffOptions(RechargeTariffOptionsReq req) {
        return chargeTariffService.showRechargeTariffOptions(req);
    }

    @Override
    public RechargeTariffDetailResp checkRechargeTariffExist(RechargeTariffExistReq req) {
        return chargeTariffService.checkRechargeTariffExist(req);
    }
}
