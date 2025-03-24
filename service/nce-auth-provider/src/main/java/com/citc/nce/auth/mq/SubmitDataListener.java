package com.citc.nce.auth.mq;

/**
 * @Author: yangchuang
 * @Date: 2022/11/1 20:46
 * @Version: 1.0
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.auth.submitdata.dao.SubmitDataDao;
import com.citc.nce.auth.submitdata.entity.SubmitDataDo;
import com.citc.nce.misc.shortUrl.ShortUrlApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 接收发送回调的Mq
 */
@Service
@RocketMQMessageListener( consumerGroup = "${rocketmq.group}", topic = "${rocketmq.topic}" )
@Slf4j
public class SubmitDataListener implements RocketMQListener<String> {
    private static final Long SUCCESS = 1L;
    @Resource
    private SubmitDataDao submitDataDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private ShortUrlApi shortUrlApi;
    /**
     * 接收回调
     * @param requestStr 回调的请求体
     */
    @Override
    public void onMessage(String requestStr) {
        //将接收到的消息转化为类
        SubmitDataDo submitDataDo = JSONObject.parseObject(requestStr, SubmitDataDo.class);
        submitDataDo.setCreateTime(new Date());
        submitDataDao.insert(submitDataDo);

        /**
         * 9.0需求
         * 表单短链接
         */
        shortUrlApi.generateUrlByIdAndType(submitDataDo.getFormId(), "01");
    }

    /**
     * 加锁
     * @param key   锁的key
     * @param val   锁的val
     * @param timeOut   获取锁的超时时间
     * @param expreTime 锁过期时间
     * @param unit      过期单位
     * @return
     */
    public boolean tryLock(String key, String val, long timeOut, long expreTime, TimeUnit unit){
        long start = System.currentTimeMillis();
        for (;;) {
            Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, val, expreTime, unit);
            if (aBoolean){
                return true;
            }
            long time = System.currentTimeMillis() - start;
            if (time > timeOut){
                return false;
            }
        }
    }

    /**
     * 解锁
     * @param key
     * @param val
     * @return
     */
    public boolean unLock(String key, String val){
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object execute = redisTemplate.execute(redisScript, Collections.singletonList(key), val);
        if(SUCCESS.equals(execute)) {
            return true;
        }
        return false;
    }
}
