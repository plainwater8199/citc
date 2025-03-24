package com.citc.nce.aim.service;

import com.citc.nce.aim.entity.AimProjectDo;
import com.citc.nce.aim.vo.AimProjectResp;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/28 18:10
 */
public interface AimDealRedisCacheService {

    int setAmountRedisCache(AimProjectDo update, AimProjectResp oldProject);

    int processRedisCache(long orderId);

    int deleteRedisCache(String projectId);

    int updateProjectRedisCache(AimProjectDo update);

    void decrementAmount(String calling, long amount);

    int setRedisCache(String projectId);
}
