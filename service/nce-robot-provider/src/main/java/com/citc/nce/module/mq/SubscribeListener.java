package com.citc.nce.module.mq;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.module.dto.SendContentForSubscribeDto;
import com.citc.nce.module.entity.SubscribeContentDo;
import com.citc.nce.module.entity.SubscribeModuleDo;
import com.citc.nce.module.enums.ModuleStatusEnums;
import com.citc.nce.module.service.*;
import com.citc.nce.utils.DateUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;

@Service
@RocketMQMessageListener( consumerGroup = "${rocketmq.subscribe.send.group}", topic = "${rocketmq.subscribe.send.topic}" )
@Slf4j
public class SubscribeListener implements RocketMQListener<String> {


    @Resource
    private SubscribeModuleService subscribeModuleService;
    @Resource
    private SubscribeContentService subscribeContentService;
    @Resource
    private SubscribeNamesService subscribeNamesService;
    @Resource
    private SubscribeContentSendDetailService subscribeContentSendDetailService;

    @Resource
    private ModuleService moduleService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate redisTemplate;


    private static final String SUBSCRIBE_ID_IN_MQ_SET_KEY = "SUBSCRIBE_ID_IN_MQ_SET";

    /**
     * 定时发送订阅内容
     * 1、相同的订阅组件和订阅时间是否已经发过一次，防止重复发送
     * 2、查询订阅组件是否正常、检查待发送时间和当前时间误差是否在1分钟
     *    a、是否已经删除
     * 3、检查是否存在订阅者
     * 4、查询所有的发送内容
     * 5、争对每个用户查询他们的该发送的内容
     * 6、则先根据用户查询各个用户的发送情况，查询待发送的订阅内容，再发送）
     * @param message 待消费的消息
     */

    @Override
    public void onMessage(String message) {
        log.info("###### SubscribeListener 开始消费mq消息 message : {} ######", message);
        SendContentForSubscribeDto sendContentForSubscribeInfo = JSONObject.parseObject(message, SendContentForSubscribeDto.class);
        String subscribeId = sendContentForSubscribeInfo.getSubscribeId();
        if(!isSend(sendContentForSubscribeInfo)){//相同的订阅组件和订阅时间是否已经发过一次
            SubscribeModuleDo subscribeModuleInfo = subscribeModuleService.getSubscribeModuleInfo(subscribeId);
            //1、查询订阅组件是否正常、检查待发送时间和当前时间误差是否在1分钟，
            if(subscribeModuleInfo.getDeleted() == 0 && DateUtils.comparison(new Date(),DateUtils.obtainDate(DateUtils.obtainDateStr(new Date(),"yyyy-MM-dd")+" "+subscribeModuleInfo.getSendTime(),"yyyy-MM-dd HH:mm"),180000L)){
                sendContentForSubscribeInfo.setSubscribeName(subscribeModuleInfo.getName());
                //2、检查是否存在订阅者
                Map<String,Map<String,String>> sendingInfoMap = subscribeNamesService.getSendPhone2ChatbotIds(subscribeId);
                if(CollUtil.isNotEmpty(sendingInfoMap)){
                    // 3、查询所有的发送内容
                    List<SubscribeContentDo> subscribeContentDos = subscribeContentService.findListById(subscribeId);
                    for(Map.Entry<String,Map<String,String>> entry : sendingInfoMap.entrySet()){
                        Map<String,String> sendPhone2ChatbotIdMap = entry.getValue();
                        //争对每个用户查询他们的该发送的内容
                        obtainSubContentInfo(entry.getKey(),subscribeContentDos,sendContentForSubscribeInfo);

                        if(sendContentForSubscribeInfo.getIsTheLast() != null){
                            // 4、则先根据用户查询各个用户的发送情况，查询待发送的订阅内容，再发送）
                            sendBatchMessageFor5G(sendPhone2ChatbotIdMap,sendContentForSubscribeInfo);
                            // 5、如是最后一期内容，且存在发送完的消息提示，直接发送消息提示，并且更新订阅组件的状态。
                            if(sendContentForSubscribeInfo.getIsTheLast()== ModuleStatusEnums.SUB_CONTENT_IS_THE_LAST.getCode() && (ModuleStatusEnums.MODULE_IS_PROMPT.getCode() == subscribeModuleInfo.getSubsEnd() && subscribeModuleInfo.getEnd5gMsgId() != null)){
                                sendContentForSubscribeInfo.setMsg5GId(subscribeModuleInfo.getEnd5gMsgId());
                                sendContentForSubscribeInfo.setTitle("已结束");
                                sendContentForSubscribeInfo.setSubContentId(null);
                                //发送结束通知
                                sendBatchMessageFor5G(sendPhone2ChatbotIdMap,sendContentForSubscribeInfo);

                            }else if(subscribeModuleInfo.getSubscribeStatus() == ModuleStatusEnums.SUB_MODULE_STATUS_TO_BE_SEND.getCode()){//将订阅组件的待发送改为已发送
                                subscribeModuleService.updateSubscribeModuleStatus(subscribeModuleInfo,ModuleStatusEnums.SUB_MODULE_STATUS_SENDING.getCode());//发送中
                            }
                        }
                    }
                }
            }
        }
        log.info("###### SubscribeListener 处理完成 ######");
    }

    /**
     * 判断是否已经发送过。
     * @param sendContentForSubscribeInfo 消息
     * @return 是否已经发送过
     */
    private boolean isSend(SendContentForSubscribeDto sendContentForSubscribeInfo) {
        String key = sendContentForSubscribeInfo.getSubscribeId()+sendContentForSubscribeInfo.getSendDate();
        RLock lock = redissonClient.getLock(key);
        try {
            lock.lock();
            boolean isHave = redisTemplate.opsForSet().isMember(SUBSCRIBE_ID_IN_MQ_SET_KEY,key);
            if(!isHave){
                redisTemplate.opsForSet().add(SUBSCRIBE_ID_IN_MQ_SET_KEY,key);
            }
            return isHave;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return true;
    }

    private void obtainSubContentInfo(String advance, List<SubscribeContentDo> subscribeContentDos, SendContentForSubscribeDto endContentForSubscribeInfo) {
        if(Strings.isNullOrEmpty(advance) || "-1".equals(advance)){//直接找到第一个待发送的
            for(SubscribeContentDo item : subscribeContentDos){
                if(item.getDeleted() == 0 && item.getDeletedTime() == null){
                    endContentForSubscribeInfo.setIsTheLast(item.getIsTheLast());
                    endContentForSubscribeInfo.setTitle(item.getTitle());
                    endContentForSubscribeInfo.setSubContentId(item.getSubContentId());
                    endContentForSubscribeInfo.setMsg5GId(item.getMsg5gId());
                    return;
                }
            }
        }else{//找指定订阅内容的下一个内容
            boolean isOK = false;
            for(SubscribeContentDo item : subscribeContentDos){
                if(isOK && (item.getDeleted() == 0 && item.getDeletedTime() == null)){
                    endContentForSubscribeInfo.setIsTheLast(item.getIsTheLast());
                    endContentForSubscribeInfo.setTitle(item.getTitle());
                    endContentForSubscribeInfo.setSubContentId(item.getSubContentId());
                    endContentForSubscribeInfo.setMsg5GId(item.getMsg5gId());
                    return;
                }

                if(advance.equals(item.getSubContentId())){
                    isOK = true;
                }

            }
        }
    }

    private void sendBatchMessageFor5G(Map<String,String> sendPhone2ChatbotIdMap, SendContentForSubscribeDto sendContentForSubscribeInfo) {
        List<String> phones = new ArrayList<>();
        for(Map.Entry<String,String> entry: sendPhone2ChatbotIdMap.entrySet()){
            String phone = entry.getKey();
            phones.add(phone);
            moduleService.sendMessage(sendContentForSubscribeInfo.getMsg5GId(),sendContentForSubscribeInfo.getSubscribeName(),phone,entry.getValue(),ModuleStatusEnums.MODULE_SUBSCRIBE.getCode());

            //更新发送进度
            if(sendContentForSubscribeInfo.getSubContentId() != null){
                subscribeNamesService.updateAdvance(phone,sendContentForSubscribeInfo.getSubscribeId(),sendContentForSubscribeInfo.getSubContentId());
            }
        }
        // 保存发送的内容
        subscribeContentSendDetailService.saveSubscribeSendRecord(phones,sendContentForSubscribeInfo);
    }

//    @PostMapping("/licQuery")
//    public void saveSubscribeContent(SubscribeContentSaveReq req) {
//        //从redis缓存中删除
//        SetOperations<String,Object> setOperations = redisTemplate.opsForSet();
//        Set<Object> members = setOperations.members(SUBSCRIBE_ID_IN_MQ_SET_KEY);
//        if(!CollectionUtils.isEmpty(members)){
//            for(Object item : members){
//                setOperations.remove(SUBSCRIBE_ID_IN_MQ_SET_KEY,item);
//            }
//        }
//        SendContentForSubscribeDto sendContentForSubscribeInfo = new SendContentForSubscribeDto();
//        sendContentForSubscribeInfo.setSubscribeId("8b168103-a8ed-4bc5-bb6c-31602c07a583");
//        onMessage(JSON.toJSONString(sendContentForSubscribeInfo));
//        onMessage(JSON.toJSONString(sendContentForSubscribeInfo));
//    }


}
