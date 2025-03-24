package com.citc.nce.im.mq;

import com.citc.nce.common.core.enums.GatewayMessageStatus;
import com.citc.nce.im.mq.service.MsgSendResponseManage;
import com.citc.nce.im.session.entity.FifthDeliveryStatusDto;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.citc.nce.common.core.enums.GatewayMessageStatus.*;


/**
 * 接收发送回调的Mq
 */
@Service
@RocketMQMessageListener(consumerGroup = "${rocketmq.5gMsg.group}", topic = "${rocketmq.5gMsg.topic}")
@Slf4j
@RequiredArgsConstructor
public class DeliveryListener implements RocketMQListener<FifthDeliveryStatusDto> {
    @Resource(name = "normalStringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    private final MsgSendResponseManage msgSendResponseManage;
    private final MsgRecordApi msgRecordApi;
    private final static String M5G_DELIVERY_STATUS_PREFIX = "5g_delivery_status:";
    private final StringRedisSerializer stringRedisSerializer;

    /**
     * 接收回调
     */
    @Override
    public void onMessage(FifthDeliveryStatusDto dto) {
        String messageId = dto.getMessageId();
        String oldMessageId = dto.getOldMessageId();
        String sender = dto.getSender();
        //用来上锁的message id
        String lockMessageId = messageId != null ? messageId : oldMessageId;
        //要更新状态的手机号列表
        List<String> phones;
        System.out.println("-------------5G消息-接收到回调消息:sender: "+ sender +" status: "+ dto.getStatus());
        if (Objects.equals(dto.getStatus(), failed.name()) && StringUtils.hasText(oldMessageId)) {
            phones = msgRecordApi.queryBatchPhoneListByMessageId(oldMessageId);
        } else if (Objects.equals(dto.getStatus(), gatewaysent.name())) {
            //如果是gatewaysent，则为每一个手机号初始化状态
            phones = msgRecordApi.queryBatchPhoneListByMessageId(oldMessageId);
        } else {
            //否则只处理当前手机号的状态
            phones = Collections.singletonList(sender);
        }
        String key = M5G_DELIVERY_STATUS_PREFIX + lockMessageId + ":" + sender;
        //当前状态
        String currentState = redisTemplate.opsForValue().get(key);
        if(currentState == null){
            lockMessageId = lockMessageId.toUpperCase();
            key = M5G_DELIVERY_STATUS_PREFIX + lockMessageId + ":" + sender;
            currentState = redisTemplate.opsForValue().get(key);
        }

        //收到的回调状态（想要更新到的状态）
        System.out.println("-------------5G消息-当前状态: "+ currentState +" 收到的回调状态: "+ dto.getStatus());
        GatewayMessageStatus nextState = GatewayMessageStatus.valueOf(dto.getStatus());
        List<String> possiblePreviousStatus = getPossiblePreviousStatus(nextState);
        //检查消息的当前状态是否是回调状态的前置状态
        if (possiblePreviousStatus.contains(currentState)) {
            //接受状态
            log.info("5G消息-接受状态回调消息:lockMessageId:{} sender: {} currentState: {} nextState: {}", lockMessageId, sender, currentState, nextState);
            for(String phone : phones) {
                System.out.println("-------------------5G消息-处理手机号: "+phone);
            }
            //调用事务方法，处理业务逻辑
            msgSendResponseManage.handleMessageStatusCallback(dto);
            //更新对应的消息状态，ddl设置为7天，5g消息状态回调最长时间为72小时(延长时间可以用来排查问题)
            Expiration expiration = Expiration.from(7, TimeUnit.DAYS);
            byte[] serializedUpdateState = stringRedisSerializer.serialize(nextState.name());
            String finalLockMessageId = lockMessageId;
            redisTemplate.executePipelined((RedisCallback<?>) connection -> {
                for (String phone : phones) {
                    String k = M5G_DELIVERY_STATUS_PREFIX + finalLockMessageId + ":" + phone;
                    connection.set(stringRedisSerializer.serialize(k), serializedUpdateState, expiration, RedisStringCommands.SetOption.UPSERT);
                }
                return null;
            });
        } else {
            //拒绝状态，等待前置状态消费后重试（更严谨做法应该继续订阅此topic的死信队列来处理重试次数用完后，仍然未能消费的消息。正常情况下每条消息最多重试4次就能消费成功）
            throw new IllegalStateException(String.format("5g消息-拒绝状态回调消息, lockMessageId: %s sender: %s currentState: %s nextState: %s", lockMessageId, sender, currentState, nextState));
        }
    }

    /**
     * 获取当前状态的全部前置状态
     */
    private List<String> getPossiblePreviousStatus(GatewayMessageStatus state) {
        List<String> possiblePreviousStatus = new ArrayList<>();
        switch (state) {
            case gatewaysent:
                possiblePreviousStatus.add(null);
                break;
            case sent:
                possiblePreviousStatus.add(gatewaysent.name());
                break;
            case delivered:
            case deliveredToNetwork:
                possiblePreviousStatus.add(sent.name());
                break;
            case displayed:
                possiblePreviousStatus.add(delivered.name());
                break;
            case failed:
                possiblePreviousStatus.add(gatewaysent.name());
                possiblePreviousStatus.add(sent.name());
                possiblePreviousStatus.add(null);
                break;
            //撤回成功只消费一次，因为会返回资源
            case revokeOk:
                possiblePreviousStatus.add(null);
                possiblePreviousStatus.addAll(Arrays.stream(values()).filter(s -> s != revokeOk).map(Enum::name).collect(Collectors.toList()));
                break;
            //撤回失败可以消费任意次
            case revokeFail:
                possiblePreviousStatus.add(null);
                possiblePreviousStatus.addAll(Arrays.stream(values()).map(Enum::name).collect(Collectors.toList()));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
        return possiblePreviousStatus;
    }
}
