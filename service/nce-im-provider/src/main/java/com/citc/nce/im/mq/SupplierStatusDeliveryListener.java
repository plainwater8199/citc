package com.citc.nce.im.mq;

import com.citc.nce.im.mq.service.FontdoMsgSendResponseManage;
import com.citc.nce.robot.req.FontdoMessageStatusReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


/**
 * 接收发送回调的Mq
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.supplier.group}", topic = "${rocketmq.supplier.topic}")
@Slf4j
@RequiredArgsConstructor
public class SupplierStatusDeliveryListener implements RocketMQListener<FontdoMessageStatusReq> {
    private final static String STATE_PENDING = "PENDING";
    private final static String STATE_SENT = "SENT";
    private final static String STATE_DELIVERED = "DELIVERED";
    private final static String STATE_DISPLAYED = "DISPLAYED";
    private final static String STATE_FALLBACK_SMS = "FALLBACK_SMS";
    private final static String STATE_FALLBACK_MMS = "FALLBACK_MMS";
    private final static String STATE_FAILED = "FAILED";

    private final RedissonClient redissonClient;
    private final static String M5G_SUPPLIER_DELIVERY_STATUS_PREFIX = "5g_supplier_delivery_status:";
    private final FontdoMsgSendResponseManage msgSendResponseManage;

    @Resource(name = "normalStringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    /**
     * 接收回调
     */
    @Override
    public void onMessage(FontdoMessageStatusReq statusReq) {
        String phone = statusReq.getNumber();
        String taskId = statusReq.getTaskId();
        RLock lock = redissonClient.getLock("supplierDeliveryStatus:" + taskId + phone);
        try {
            lock.lock();
            String updateState = statusReq.getStatus();
            List<String> previousStatus = getPossiblePreviousStatus(updateState);
            String stateKey = M5G_SUPPLIER_DELIVERY_STATUS_PREFIX + taskId + ":" + phone;
            String currState = redisTemplate.opsForValue().get(stateKey);
            if (previousStatus.contains(currState)) {
                //接受状态 调用事务方法，处理业务逻辑
                log.info("蜂动5G消息-消费状态回调:taskId: {} phone: {} currentState: {} updateState: {}", taskId, phone, currState, updateState);
                msgSendResponseManage.msgSendResponseAsynManageFor5G(statusReq);
                //处理成功，修改状态
                redisTemplate.opsForValue().set(stateKey, updateState, Duration.ofDays(7));
            } else {
                throw new IllegalStateException(String.format("蜂动5g消息-拒绝状态回调, taskId: %s phone: %s currentState: %s updateState: %s", taskId, phone, currState, updateState));
            }
        }
        finally {
            //释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


    private List<String> getPossiblePreviousStatus(String state) {
        List<String> possiblePreviousStatus = new ArrayList<>();
        state = state.toUpperCase();
        switch (state) {
            case STATE_PENDING:
            case STATE_SENT:
                possiblePreviousStatus.add(null);
                break;
            case STATE_DELIVERED:
            case STATE_FALLBACK_SMS:
            case STATE_FALLBACK_MMS:
            case STATE_FAILED:
                possiblePreviousStatus.add(STATE_SENT);
                break;
            case STATE_DISPLAYED:
                possiblePreviousStatus.add(STATE_DELIVERED);
                break;
        }
        return possiblePreviousStatus;
    }
}
