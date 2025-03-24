package com.citc.nce.aim.privatenumber.service;

import com.citc.nce.aim.privatenumber.entity.PrivateNumberOrderDo;
import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

import java.util.List;
import java.util.Set;


public interface PrivateNumberOrderService {

    int save(PrivateNumberOrderSaveReq req);

    int edit(PrivateNumberOrderEditReq req);

    int updateStatus(PrivateNumberOrderUpdateStatusReq req);

    int delete(PrivateNumberOrderDeleteReq req);

    PrivateNumberOrderResp queryOrderById(PrivateNumberOrderQueryReq req);

    PrivateNumberOrderResp queryEnabledOrderByProjectId(PrivateNumberOrderQueryReq req);

    PageResult<PrivateNumberOrderResp> queryOrderList(PrivateNumberOrderQueryListReq req);

    PrivateNumberOrderResp queryEnabledOrderByProjectId(String projectId);

    List<PrivateNumberOrderDo> queryOrderListByOrderIds(Set<Long> orderIds);

    void updateBatch(List<PrivateNumberOrderDo> updateList);
}
