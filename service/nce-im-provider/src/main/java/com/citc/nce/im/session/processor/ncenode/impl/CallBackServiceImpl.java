package com.citc.nce.im.session.processor.ncenode.impl;

import com.citc.nce.im.session.processor.ncenode.CallBackService;
import com.citc.nce.robot.vo.RobotCallBackParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>回调函数实现</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/1 11:17
 */
@Service
@Slf4j
public class CallBackServiceImpl implements CallBackService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final Long OVER_TIME = 60L;

    @Override
    public void callBackMethod(RobotCallBackParam callBackParam) {
        log.info("CallBackServiceImpl callBackMethod is start");

        // 成功返回数据
        if (StringUtils.isNotEmpty(callBackParam.getRedisKey())) {
            stringRedisTemplate.opsForValue().set(callBackParam.getRedisKey() + "Execute", callBackParam.getCode().toString(), OVER_TIME, TimeUnit.SECONDS);
        } else {
            log.info("CallBackServiceImpl callBackMethod 参数异常 {}", callBackParam);
        }
    }
}
