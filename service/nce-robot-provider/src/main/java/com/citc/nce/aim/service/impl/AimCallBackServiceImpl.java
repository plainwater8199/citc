package com.citc.nce.aim.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.constant.AimOrderStatusEnum;
import com.citc.nce.aim.dao.AimSentDataDao;
import com.citc.nce.aim.dto.AimUserSettings;
import com.citc.nce.aim.entity.AimSentDataDo;
import com.citc.nce.aim.service.AimCallBackService;
import com.citc.nce.aim.service.AimOrderService;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.vo.*;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.configure.RocketMQConfigure;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author
 * @CreatedTime 2023/6/8 18:05
 */
@Service
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties(RocketMQConfigure.class)
public class AimCallBackServiceImpl implements AimCallBackService {

    @Resource
    private AimOrderService aimOrderService;
    @Resource
    private AimProjectService aimProjectService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private AimSentDataDao aimSentDataDao;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private final RocketMQConfigure rocketMQConfigure;
    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    @Override
    public AimCallBackResp queryProjectInfo(String calling) {
        AimCallBackResp resp = new AimCallBackResp();
        resp.setCode(200);
        resp.setMsg(AimConstant.DEFAULT_MSG_SUCCESS);
        JSONObject data = new JSONObject();
        AimProjectQueryReq req = new AimProjectQueryReq();
        req.setCalling(calling);
        req.setProjectStatus(1);
        AimProjectResp aimProjectResp = aimProjectService.queryProject(req);
        if (null != aimProjectResp) {
            AimOrderQueryReq orderQueryReq = new AimOrderQueryReq();
            orderQueryReq.setProjectId(aimProjectResp.getProjectId());
            AimOrderResp aimOrderResp = aimOrderService.queryEnabledOrderByProjectId(orderQueryReq);
            if (null != aimOrderResp) {
                long differ = aimOrderResp.getOrderAmount() - aimOrderResp.getOrderConsumption();
                if (differ > 0L) {
                    redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + aimOrderResp.getProjectId(), String.valueOf(differ));
                    data.put("projectId", aimOrderResp.getProjectId());
                    data.put("orderId", aimOrderResp.getId());
                    data.put("calling", aimProjectResp.getCalling());
                    data.put("smsTemplate", aimProjectResp.getSmsTemplate());
                    data.put("type", AimConstant.SMS_TYPE);
                    data.put("key", aimProjectResp.getPathKey());
                    data.put("pw", aimProjectResp.getSecret());
                    redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_SMS_PREFIX + aimProjectResp.getCalling(), data.toJSONString());
                }
            }
        }
        AimUserSettings userSettings = JsonUtils.string2Obj(data.toJSONString(), AimUserSettings.class);
        resp.setData(userSettings);
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AimCallBackResp smsCallBack(List<AimCallBack> req) {
        log.info("###### aim 挂短回调 需要处理的数据 : {} ######", req);
        AimCallBackResp resp = new AimCallBackResp();
        resp.setCode(200);
        resp.setMsg(AimConstant.DEFAULT_MSG_SUCCESS);
        if (CollectionUtil.isNotEmpty(req)) {
            List<AimCallBack> callBackList = req;
            List<AimSentDataDo> insertList = new ArrayList<>();
            for (AimCallBack callBack :
                    callBackList) {
                AimSentDataDo dataDo = new AimSentDataDo();
                BeanUtil.copyProperties(callBack, dataDo);
                dataDo.setCreator(AimConstant.INSERT_USER);
                dataDo.setUpdater(AimConstant.INSERT_USER);
                dataDo.setCreateTime(DateUtils.obtainDate(callBack.getSendTime()));
                insertList.add(dataDo);
            }
            try {
                if (CollectionUtil.isNotEmpty(insertList)) {
                    aimSentDataDao.insertBatch(insertList);
                }
                threadPool.execute(() -> processRedisCache(callBackList));
            } catch (RuntimeException e) {
                log.info(e.getMessage());
                resp.setMsg(AimConstant.DEFAULT_MSG_FAIL);
            }
        }
        return resp;
    }

    private void processRedisCache(List<AimCallBack> callBackList) {
        List<Long> orderIdList = new ArrayList<>();
        for (AimCallBack callBack :
                callBackList) {
            // 更新缓存
            Object obj = redisTemplate.opsForValue().get(AimConstant.REDIS_KEY_AMOUNT_PREFIX + callBack.getProjectId());
            long increment = 1L;
            if (Objects.nonNull(obj)) {
                increment = Long.parseLong(obj.toString()) - 1L;
                redisTemplate.opsForValue().set(AimConstant.REDIS_KEY_AMOUNT_PREFIX + callBack.getProjectId(), increment);
            } else {
                redisTemplate.delete(AimConstant.REDIS_KEY_SMS_PREFIX + callBack.getCalling());
            }
            if (0L >= increment) {
                redisTemplate.delete(AimConstant.REDIS_KEY_AMOUNT_PREFIX + callBack.getProjectId());
                AimOrderUpdateStatusReq req = new AimOrderUpdateStatusReq();
                req.setId(callBack.getOrderId());
                req.setOrderStatus(AimOrderStatusEnum.COMPLETED.getCode());
                aimOrderService.updateStatus(req);
            }
            orderIdList.add(callBack.getOrderId());
        }
        if (CollectionUtil.isNotEmpty(orderIdList)) {
            String requestStr = JSONObject.toJSONString(orderIdList);
            Message<String> message = MessageBuilder.withPayload(requestStr).build();

            //同步发送该消息，获取发送结果
            SendResult sendResult = rocketMQTemplate.syncSend(rocketMQConfigure.getTopic(), message);
            log.info("AimCallBackServiceImpl 发送到mq 结果为 ：{}", sendResult);
        }
    }
}
