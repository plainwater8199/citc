package com.citc.nce.aim.mq;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.constant.AimConstant;
import com.citc.nce.aim.privatenumber.dao.PrivateNumberSentDataDao;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberOrderDo;
import com.citc.nce.aim.privatenumber.entity.PrivateNumberSentDataDo;
import com.citc.nce.aim.privatenumber.service.PrivateNumberDealRedisCacheService;
import com.citc.nce.aim.privatenumber.service.PrivateNumberOrderService;
import com.citc.nce.aim.privatenumber.vo.PrivateNumberCallBack;
import com.citc.nce.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "${rocketmq.aimprivatenumber.group}", topic = "${rocketmq.aimprivatenumber.topic}")
@Slf4j
public class PrivateNumberCallBackListener implements RocketMQListener<String> {
    @Resource
    private PrivateNumberOrderService privateNumberOrderService;
    @Resource
    private PrivateNumberSentDataDao privateNumberSentDataDao;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private PrivateNumberDealRedisCacheService privateNumberDealRedisCacheService;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void onMessage(String message) {
        log.info("###### PrivateNumberCallBackListener 开始消费mq消息 message : {} ######", message);
        // 所有需要扣减额度的订单
        List<PrivateNumberCallBack> callBackList = JSONObject.parseArray(message, PrivateNumberCallBack.class);

        messageTest(callBackList);
        log.info("###### PrivateNumberCallBackListener 消费mq消息结束 ######");
    }

    public void messageTest(List<PrivateNumberCallBack> callBackList) {
        if (CollectionUtil.isNotEmpty(callBackList)) {
            List<PrivateNumberSentDataDo> insertList = new ArrayList<>();
            for (PrivateNumberCallBack callBack : callBackList) {
                PrivateNumberSentDataDo dataDo = new PrivateNumberSentDataDo();
                BeanUtil.copyProperties(callBack, dataDo);
                dataDo.setCreator(AimConstant.INSERT_USER);
                dataDo.setUpdater(AimConstant.INSERT_USER);
                dataDo.setCreateTime(DateUtils.obtainDate(callBack.getSendTime()));
                insertList.add(dataDo);
            }
            try {
                privateNumberSentDataDao.insertBatch(insertList);
                deductRedisBalance(callBackList);
                //更改订单状态
                log.info("###### 更新订单 ######");
                updateOrders(callBackList);
            } catch (Exception e) {
                log.error("###### 更新订单失败,每条信息单独尝试 ######", e);
                List<PrivateNumberSentDataDo> successList = CollectionUtil.newArrayList();
                List<PrivateNumberSentDataDo> warnList = CollectionUtil.newArrayList();
                for (PrivateNumberSentDataDo privateNumberSentDataDo : insertList) {
                    try {
                        privateNumberSentDataDao.insert(privateNumberSentDataDo);
                        successList.add(privateNumberSentDataDo);
                    } catch (DuplicateKeyException duplicateKeyException) {
                        log.warn("重复接收,跳过", duplicateKeyException);
                    } catch (Exception exception) {
                        log.warn("其他严重错误,DataDo:{}", privateNumberSentDataDo, exception);
                        warnList.add(privateNumberSentDataDo);
                    }
                }
                deductRedisBalanceCompensate(successList);
                if (CollectionUtil.isNotEmpty(warnList)) {
                    log.error("warnList: {}", warnList);
                }
            }

        }
    }

    //扣除redis 余额,可能删除redis短信配置
    private void deductRedisBalance(List<PrivateNumberCallBack> req) {
        Map<String, List<PrivateNumberCallBack>> groupedByAppKey = req.stream().filter(item -> item.getStatus() == 1).collect(Collectors.groupingBy(PrivateNumberCallBack::getAppKey));
        for (Map.Entry<String, List<PrivateNumberCallBack>> entry : groupedByAppKey.entrySet()) {
            String appKey = entry.getKey();
            List<PrivateNumberCallBack> callBackList = entry.getValue();
            Map<Long, List<PrivateNumberCallBack>> groupedByOrderId = callBackList.stream().collect(Collectors.groupingBy(PrivateNumberCallBack::getOrderId));
            for (Map.Entry<Long, List<PrivateNumberCallBack>> orderEntry : groupedByOrderId.entrySet()) {
                Long orderId = orderEntry.getKey();
                List<PrivateNumberCallBack> callBackListByOrder = orderEntry.getValue();
                int size = callBackListByOrder.size();
                RLock lock = redissonClient.getLock(AimConstant.PRIVATE_NUMBER_REDIS_KEY_ORDER_KEY + orderId);
                try {
                    //订单维度分布式锁
                    lock.lock();
                    //查询appKey 某订单的余额
                    Object stock = redisTemplate.opsForValue().get(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + appKey + ":" + orderId);
                    //为空代表该订单已经使用完或者不生效,不需要进行处理
                    //不为空代表该订单正在使用状态,且有余额
                    if (stock != null) {
                        long stockInt = Long.parseLong(stock.toString());
                        //足够扣除
                        if (stockInt > size) {
                            redisTemplate.opsForValue().decrement(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + appKey + ":" + orderId, size);
                        } else {
                            //不足扣除或正好使用完余额,删除短信配置和appKey该订单余额
                            privateNumberDealRedisCacheService.deleteRedisCache(appKey, orderId);
                        }
                    }
                } catch (Exception e) {
                    log.error("AimCallBackServiceImpl smsCallBack error", e);
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    //扣除redis 余额,可能删除redis短信配置
    private void deductRedisBalanceCompensate(List<PrivateNumberSentDataDo> req) {
        Map<String, List<PrivateNumberSentDataDo>> groupedByAppKey = req.stream().collect(Collectors.groupingBy(PrivateNumberSentDataDo::getAppKey));
        for (Map.Entry<String, List<PrivateNumberSentDataDo>> entry : groupedByAppKey.entrySet()) {
            String appKey = entry.getKey();
            List<PrivateNumberSentDataDo> callBackList = entry.getValue();
            Map<Long, List<PrivateNumberSentDataDo>> groupedByOrderId = callBackList.stream().collect(Collectors.groupingBy(PrivateNumberSentDataDo::getOrderId));
            for (Map.Entry<Long, List<PrivateNumberSentDataDo>> orderEntry : groupedByOrderId.entrySet()) {
                Long orderId = orderEntry.getKey();
                List<PrivateNumberSentDataDo> callBackListByOrder = orderEntry.getValue();
                int size = callBackListByOrder.size();
                RLock lock = redissonClient.getLock(AimConstant.PRIVATE_NUMBER_REDIS_KEY_ORDER_KEY + orderId);
                try {
                    //订单维度分布式锁
                    lock.lock();
                    //查询appKey 某订单的余额
                    Object stock = redisTemplate.opsForValue().get(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + appKey + ":" + orderId);
                    //为空代表该订单已经使用完或者不生效,不需要进行处理
                    //不为空代表该订单正在使用状态,且有余额
                    if (stock != null) {
                        long stockInt = Long.parseLong(stock.toString());
                        //足够扣除
                        if (stockInt > size) {
                            redisTemplate.opsForValue().decrement(AimConstant.PRIVATE_NUMBER_REDIS_KEY_AMOUNT_PREFIX + appKey + ":" + orderId, size);
                        } else {
                            //不足扣除或正好使用完余额,删除短信配置和appKey该订单余额
                            privateNumberDealRedisCacheService.deleteRedisCache(appKey, orderId);
                        }
                    }
                } catch (Exception e) {
                    log.error("AimCallBackServiceImpl smsCallBack error", e);
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    private void updateOrders(List<PrivateNumberCallBack> callBackList) {
        //更新订单状态
        Map<Long, List<PrivateNumberCallBack>> groupedByAppKey = callBackList.stream().filter(item -> item.getStatus() == 1).collect(Collectors.groupingBy(PrivateNumberCallBack::getOrderId));
        //查询订单
        List<PrivateNumberOrderDo> orderIdList = privateNumberOrderService.queryOrderListByOrderIds(groupedByAppKey.keySet());
        if (CollectionUtil.isNotEmpty(orderIdList)) {
            //更新订单状态
            List<PrivateNumberOrderDo> updateList = new ArrayList<>();
            Map<Long, PrivateNumberOrderDo> orderMap = orderIdList.stream().collect(Collectors.toMap(PrivateNumberOrderDo::getId, Function.identity()));
            for (Long orderId : groupedByAppKey.keySet()) {
                int decrementCount = groupedByAppKey.get(orderId).size();
                PrivateNumberOrderDo privateNumberOrderDo = orderMap.get(orderId);
                privateNumberOrderDo.setOrderConsumption(privateNumberOrderDo.getOrderConsumption() + decrementCount);
                if (privateNumberOrderDo.getOrderConsumption() >= privateNumberOrderDo.getOrderAmount()) {
                    privateNumberOrderDo.setOrderStatus(2);
                }
                updateList.add(privateNumberOrderDo);
            }
            privateNumberOrderService.updateBatch(updateList);
        }
    }
}
