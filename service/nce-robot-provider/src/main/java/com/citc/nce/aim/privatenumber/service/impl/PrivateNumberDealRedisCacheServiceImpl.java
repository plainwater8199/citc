package com.citc.nce.aim.privatenumber.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimError;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDealRedisCacheService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberOrderService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberProjectService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderQueryReq;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberOrderResp;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberProjectInfo;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class PrivateNumberDealRedisCacheServiceImpl implements PrivateNumberDealRedisCacheService {

    @Resource
    private PrivateNumberOrderService privateNumberOrderService;

    @Resource
    PrivateNumberProjectService privateNumberProjectService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void deleteRedisCache(String appKey, Long orderId) {
        try {
            if (orderId != null) {
                redisTemplate.delete(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + appKey + ":" + orderId);

                PrivateNumberOrderQueryReq orderQueryReq = new PrivateNumberOrderQueryReq();
                orderQueryReq.setId(orderId);
                PrivateNumberOrderResp orderResp = privateNumberOrderService.queryOrderById(orderQueryReq);
                if (orderResp != null && StringUtils.isNotEmpty(orderResp.getProjectId())) {
                    PrivateNumberProjectDo projectDo = privateNumberProjectService.queryByProjectId(orderResp.getProjectId());
                    if (projectDo != null && StringUtils.isNotEmpty(projectDo.getEventType())) {
                        redisTemplate.delete(AimConstant.PRIVATE_NUMBER_REDIS_KEY_SMS_PREFIX + appKey + ":" + projectDo.getEventType());
                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BizException(AimError.REDIS_EXCEPTION);
        }
    }

    @Override
    public PrivateNumberCallBackResp updateProjectRedisCache(PrivateNumberProjectDo privateNumberProjectDo) {
        PrivateNumberCallBackResp resp = new PrivateNumberCallBackResp();
        if (privateNumberProjectDo != null) {
            //查询有效的订单
            PrivateNumberOrderResp validOrder = privateNumberOrderService.queryEnabledOrderByProjectId(privateNumberProjectDo.getProjectId());
            if (validOrder != null) {
                if (validOrder.getOrderAmount() > validOrder.getOrderConsumption()) {
                    PrivateNumberProjectInfo data = new PrivateNumberProjectInfo();
                    BeanUtil.copyProperties(privateNumberProjectDo, data);
                    data.setOrderId(validOrder.getId());
                    data.setType(privateNumberProjectDo.getChannelType());
                    data.setKey(privateNumberProjectDo.getPathKey());
                    data.setPw(privateNumberProjectDo.getSecret());
                    resp.setData(data);
                    //设置redis缓存
                    redisTemplate.opsForValue().set(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + privateNumberProjectDo.getAppKey() + ":" + validOrder.getId(), validOrder.getOrderAmount() - validOrder.getOrderConsumption());
                    redisTemplate.opsForValue().set(AimConstant.PRIVATE_NUMBER_REDIS_KEY_SMS_PREFIX + privateNumberProjectDo.getAppKey() + ":" + privateNumberProjectDo.getEventType(), JsonUtils.obj2String(data));
//                    if(StrUtil.equals(Constants.CHANNEL_TYPE_FONTDO,privateNumberProjectDo.getChannelType())) {
//                        redisTemplate.opsForValue().set(AimConstant.PRIVATE_NUMBER_REDIS_KEY_FONTD0_SECRET + privateNumberProjectDo.getAppId(), privateNumberProjectDo.getAppSecret());
//                    }

                } else {
                    resp.setMsg(AimError.ORDER_NOT_AMOUNT.getMsg());
                }
            } else {
                resp.setMsg(AimError.ORDER_NOT_EXIST.getMsg());
            }
        } else {
            resp.setMsg(AimError.PROJECT_NOT_EXIST.getMsg());
        }
        return resp;
    }
}
