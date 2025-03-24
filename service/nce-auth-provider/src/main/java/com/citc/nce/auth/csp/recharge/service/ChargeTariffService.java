package com.citc.nce.auth.csp.recharge.service;

/**
 * @author yy
 * @date 2024-10-16 17:43:44
 */

import com.citc.nce.auth.csp.recharge.vo.RechargeTariffAdd;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffDetailResp;
import com.citc.nce.auth.csp.recharge.vo.RechargeTariffOptionsReq;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.recharge.vo.*;

/**
 * 充值资费操作接口
 */
public interface ChargeTariffService {
    /**
     * 新增资费
     */
    void addRechargeTariff(RechargeTariffAdd req);

    /**
     * 查询资费
     */
    RechargeTariffDetailResp getRechargeTariff(String accountId);

    /**
     * 查询资费配置
     */
    TariffOptions showRechargeTariffOptions(RechargeTariffOptionsReq req);


    RechargeTariffDetailResp getById(Long tariffId);

    RechargeTariffDetailResp checkRechargeTariffExist(RechargeTariffExistReq req);
}
