package com.citc.nce.aim.mq;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.aim.service.AimOrderService;
import com.citc.nce.aim.vo.AimOrderResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/16 10:51
 */
@Service
@RocketMQMessageListener( consumerGroup = "${rocketmq.group}", topic = "${rocketmq.topic}" )
@Slf4j
public class AimCallBackListener implements RocketMQListener<String> {
    @Resource
    private AimOrderService aimOrderService;

    @Override
    public void onMessage(String message) {
        log.info("###### AimCallBackListener 开始消费mq消息 message : {} ######", message);
        // 所有需要扣减额度的订单
        List<Long> orderIdList = JSONObject.parseArray(message, Long.class);
        if (CollectionUtil.isEmpty(orderIdList)) {
            return;
        }
        // 有相同Id
        // 初始化map
        Map<Long, Long> idCountMap = new HashMap<>();
        for (long id:
                orderIdList) {
            Long count = idCountMap.get(id);
            if (null == count) {
                idCountMap.put(id, 1L);
            } else {
                idCountMap.put(id, count + 1L);
            }
        }
        // 一次性获取所有需要扣减消耗量的订单
        List<AimOrderResp> respList = aimOrderService.queryOrderByIdList(orderIdList.stream().distinct().collect(Collectors.toList()));
        if (CollectionUtil.isNotEmpty(orderIdList) && CollectionUtil.isNotEmpty(respList)) {
            for (AimOrderResp orderResp:
                    respList) {
                // 消耗量
                long consumption = orderResp.getOrderConsumption() + idCountMap.get(orderResp.getId());
                // 更新当前订单的消耗量
                aimOrderService.updateOrderConsumption(orderResp.getId(), consumption);
            }
        }
        log.info("###### AimCallBackListener 处理完成 ######");
    }
}
