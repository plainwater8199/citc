package com.citc.nce.aim.privatenumber.service;


import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;


public interface PrivateNumberDealRedisCacheService {

    void deleteRedisCache(String appKey,Long orderId);

    PrivateNumberCallBackResp updateProjectRedisCache(PrivateNumberProjectDo privateNumberProjectDo);

}
