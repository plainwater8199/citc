package com.citc.nce.authcenter.dyz.impl;

import com.citc.nce.authcenter.config.DyzConfigure;
import com.citc.nce.authcenter.dyzCallBack.vo.DyzCollBack;
import com.citc.nce.authcenter.utils.DyzSystemUtils;
import com.citc.nce.authcenter.utils.SignUtil;
import com.citc.nce.common.redis.config.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * bydud
 * 2024/1/12
 **/
@Component
@Slf4j
public class DyzService {

    @Autowired
    private DyzSystemUtils dyzSystemUtils;
    @Resource
    private RedisService redisService;
    @Autowired
    private DyzConfigure dyzConfigure;

    public Integer userCheck(DyzCollBack dyzCollBack) {
        String bizSn = dyzCollBack.getBizSn();
        String mobile = dyzCollBack.getMobile();
        try {
            if (SignUtil.verify(dyzConfigure.getDyzPlatformPubKey(),Base64Utils.decodeFromString(dyzCollBack.getSign()),
                    bizSn.getBytes(StandardCharsets.UTF_8))) {
                String userCheckRedisKey = dyzSystemUtils.getUserCheckRedisKey(bizSn);
                String redisMobile = redisService.getCacheObject(userCheckRedisKey);
                if (StringUtils.hasLength(redisMobile) && redisMobile.equals(mobile)) {
                    return 0;
                }
                return 1;
            }
        } catch (Exception e) {
            log.error("dyz callback 异常", e);
        }
        return 1;
    }


}
