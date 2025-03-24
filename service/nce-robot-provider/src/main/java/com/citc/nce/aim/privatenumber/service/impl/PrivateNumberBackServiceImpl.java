package com.citc.nce.aim.privatenumber.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberProjectDo;
import com.citc.nce.aim.privatenumber.service.PrivateNumberBackService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDealRedisCacheService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberProjectService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBackResp;
import com.citc.nce.configure.PrivateNumberRocketMQConfigure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(PrivateNumberRocketMQConfigure.class)
public class PrivateNumberBackServiceImpl implements PrivateNumberBackService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private PrivateNumberProjectService privateNumberProjectService;
    @Resource
    private PrivateNumberDealRedisCacheService privateNumberDealRedisCacheService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private final PrivateNumberRocketMQConfigure privateNumberRocketMQConfigure;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public PrivateNumberCallBackResp queryProjectInfo(String appKey) {
        PrivateNumberCallBackResp resp = new PrivateNumberCallBackResp();
        resp.setCode(200);
        //查询有效的项目
        PrivateNumberProjectDo validProject = privateNumberProjectService.queryValidProject(appKey);
        resp = privateNumberDealRedisCacheService.updateProjectRedisCache(validProject);
        return resp;
    }

    @Override
    public PrivateNumberCallBackResp smsCallBack(List<PrivateNumberCallBack> req) {
        log.info("###### AimCallBackServiceImpl 隐私号项目 需要处理的数据 : {}", req);
        PrivateNumberCallBackResp resp = new PrivateNumberCallBackResp();
        resp.setCode(200);
        resp.setMsg(AimConstant.DEFAULT_MSG_SUCCESS);
        if (CollectionUtil.isNotEmpty(req)) {
            //mq更新数据
            String requestStr = JSONObject.toJSONString(req);
            Message<String> message = MessageBuilder.withPayload(requestStr).build();
            //同步发送该消息，获取发送结果
            SendResult sendResult = rocketMQTemplate.syncSend(privateNumberRocketMQConfigure.getTopic(), message);
            log.info("privateNumberRocketMQConfigure.getTopic() ：{}，", privateNumberRocketMQConfigure.getTopic());
            log.info("privateNumberRocketMQConfigure.getGroup() ：{}，", privateNumberRocketMQConfigure.getGroup());
            log.info("AimCallBackServiceImpl 发送到mq 结果为 ：{}", sendResult);
        }
        return resp;
    }
}
