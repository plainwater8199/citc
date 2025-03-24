package com.citc.nce.authcenter.utils.service.impl;

import com.citc.nce.authcenter.constant.AuthCenterError;
import com.citc.nce.authcenter.utils.service.UtilsService;
import com.citc.nce.authcenter.utils.service.config.CheckIPVisitCountConfig;
import com.citc.nce.common.core.exception.BizException;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(CheckIPVisitCountConfig.class)
public class UtilsServiceImpl implements UtilsService {

    private final CheckIPVisitCountConfig checkIPVisitCountConfig;

    @Resource
    private RedisTemplate redisTemplate;
    @Override
    public void checkIPVisitCount(String ip) {
        if(!Strings.isNullOrEmpty(ip)){
            String key = "visitorCheck:"+ip;
            Integer count = (Integer)redisTemplate.opsForValue().get(key);
            System.out.println("----------------:"+count);
            if(null == count){
                redisTemplate.opsForValue().set(key,1,checkIPVisitCountConfig.getTimeSpan(),TimeUnit.SECONDS);
            }else{
                if(count <= checkIPVisitCountConfig.getTimes()){
                    redisTemplate.opsForValue().increment(key,1);
                }else{
                    throw new BizException(AuthCenterError.VISITOR_LIMIT);
                }
            }
        }
    }
}
