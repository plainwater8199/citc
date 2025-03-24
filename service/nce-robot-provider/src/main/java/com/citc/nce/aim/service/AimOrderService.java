package com.citc.nce.aim.service;

import com.citc.nce.aim.vo.AimOrderDeleteReq;
import com.citc.nce.aim.vo.AimOrderEditReq;
import com.citc.nce.aim.vo.AimOrderQueryListReq;
import com.citc.nce.aim.vo.AimOrderQueryReq;
import com.citc.nce.aim.vo.AimOrderResp;
import com.citc.nce.aim.vo.AimOrderSaveReq;
import com.citc.nce.aim.vo.AimOrderUpdateStatusReq;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:00
 */
public interface AimOrderService {
    int save(AimOrderSaveReq req);

    int edit(AimOrderEditReq req);

    int delete(AimOrderDeleteReq req);

    int updateOrderConsumption(long orderId, long Consumption);

    AimOrderResp queryOrderById(AimOrderQueryReq req);

    List<AimOrderResp> queryOrderByIdList(List<Long> orderIdList);

    AimOrderResp queryEnabledOrderByProjectId(AimOrderQueryReq req);

    PageResult<AimOrderResp> queryOrderList(AimOrderQueryListReq req);

    int updateStatus(AimOrderUpdateStatusReq req);
}
