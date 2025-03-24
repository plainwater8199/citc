package com.citc.nce.im.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.module.ModuleApi;
import com.citc.nce.module.SignNamesApi;
import com.citc.nce.module.SubscribeNamesApi;
import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.module.vo.req.SubscribeNamesReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.im.entity.RobotClickResult;
import com.citc.nce.im.entity.RobotPhoneUplinkResult;
import com.citc.nce.im.exp.SendGroupExp;
import com.citc.nce.im.mapper.RobotClickResultMapper;
import com.citc.nce.im.mapper.RobotPhoneUplinkResultMapper;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.enums.ButtonType;
import com.citc.nce.robot.req.DeliveryStatusReq;
import com.citc.nce.robot.vo.DeliveryMessage;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.robot.vo.RobotShortcutButtonResp;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DeliveryNoticeService  {

    @Resource
    RobotPhoneUplinkResultMapper uplinkResultMapper;

    @Resource
    RedissonClient redissonClient;

    @Resource
    ModuleApi moduleApi;

    @Resource
    RobotClickResultMapper robotClickResultMapper;

    @Resource
    RobotGroupSendPlansDetailService detailService;

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;


    @Resource
    RebotSettingApi rebotSettingApi;

    @Resource
    SubscribeNamesApi subscribeNamesApi;

    @Resource
    SignNamesApi signNamesApi;

    /**
     * 保存按钮回调的信息
     * @param deliveryMessage 按钮回调接口的信息
     */
    @Transactional
    public void saveButtonDelivery(DeliveryMessage deliveryMessage, String chatbotId) {
        RLock lock = redissonClient.getLock("buttonMessage");
        try {
            List<Integer> buttonTypeList = Arrays.asList(ButtonType.SUBSCRIBE_BTN.getCode(), ButtonType.CANCEL_SUBSCRIBE_BTN.getCode(), ButtonType.JOIN_SIGN_BTN.getCode(), ButtonType.SIGN_BTN.getCode());
            if (!deliveryMessage.getMessageData().contains("#&#&")){
                Integer btnType = queryButtonType(deliveryMessage.getMessageData());
                if (btnType != null && buttonTypeList.contains(btnType)) {
                    //如果时组件按钮-（订阅/取消订阅、打卡/取消打卡），则更新组件名单
                    moduleApi.updateModuleHandle(btnType, deliveryMessage.getMessageData(), deliveryMessage.getSender(),chatbotId);
                }
                return;
            }
            lock.lock();
            List<String> list = Arrays.asList(deliveryMessage.getMessageData().split("#&#&"));
            log.info("request ButtonMessage is {} chatbotId is {}", deliveryMessage,chatbotId);
            String butUuid = list.get(0);
            Long detailId =Long.valueOf(list.get(1));
            //1 悬浮按钮 2 卡片按钮
            int btnType = Integer.parseInt(list.get(2));
            String buttonText = list.get(3);
            int cardNum = Integer.parseInt(list.get(4));
            //根据按钮+群发计划+电话号 查询是否有这条上行消息
            //如果时组件按钮-（订阅/取消订阅、打卡/取消打卡），则更新组件名单
            if (buttonTypeList.contains(btnType)) {
                //如果时组件按钮-（订阅/取消订阅、打卡/取消打卡），则更新组件名单
                moduleApi.updateModuleHandle(btnType, butUuid, deliveryMessage.getSender(),chatbotId);
            }

            LambdaQueryWrapper<RobotPhoneUplinkResult> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RobotPhoneUplinkResult::getBtnUuid,butUuid);
            wrapper.eq(RobotPhoneUplinkResult::getPlanDetailId,detailId);
            wrapper.eq(RobotPhoneUplinkResult::getPhoneNum,deliveryMessage.getSender());
            RobotPhoneUplinkResult existence = uplinkResultMapper.selectOne(wrapper);

            //查询群发消息的创建人(机器人消息的话是不是就有问题???)
            String creator = detailService.queryById(detailId).getCreator();

            //统计到统计表(时间段的按钮点击数量统计表)
            RobotClickResult robotClickResult = new RobotClickResult();
            Date date = new Date();
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String sendDay = dayFormat.format(date);
            SimpleDateFormat hourFormat = new SimpleDateFormat("HH:00");
            Date day = dayFormat.parse(sendDay);
            String sendHour = hourFormat.format(date);
            robotClickResult.setBtnUuid(butUuid);
            robotClickResult.setSendTimeDay(day);
            robotClickResult.setSendTimeHour(sendHour);
            robotClickResult.setPlanDetailId(detailId);

            //根据按钮+群发计划+电话号判断,早已存在这条上行消息(N次点击),更新信息后不再往下
            if (!ObjectUtil.isEmpty(existence)){
                //时间段的按钮的点击数
                LambdaQueryWrapper<RobotClickResult> clickWrapper = new LambdaQueryWrapper<>();
                clickWrapper.eq(RobotClickResult::getBtnUuid,butUuid);
                clickWrapper.eq(RobotClickResult::getPlanDetailId,detailId);
                clickWrapper.eq(RobotClickResult::getSendTimeDay,day);
                clickWrapper.eq(RobotClickResult::getSendTimeHour,sendHour);
                List<RobotClickResult> selectList = robotClickResultMapper.selectList(clickWrapper);
                if (!CollectionUtils.isEmpty(selectList)){
                    //执行更新操作
                    RobotClickResult selectOne = selectList.get(0);
                    selectOne.setClickAmount(selectOne.getClickAmount() + 1);
                    robotClickResultMapper.updateById(selectOne);
                }
                //时间段的按钮的点击统计不存在!!
                else {
                    //新增操作
                    //悬浮按钮
                    if (btnType == 1){
                        robotClickResult.setBtnName("悬浮按钮-"+buttonText);
                    }//卡片按钮
                    else if (btnType == 2){
                        if (cardNum == -1){
                            //单卡按钮
                            robotClickResult.setBtnName(buttonText);
                        }else {
                            //卡片按钮
                            robotClickResult.setBtnName("卡片"+cardNum +"-"+buttonText);
                        }
                    }
                    robotClickResult.setClickAmount(1L);
                    robotClickResult.setCreator(creator);
                    robotClickResultMapper.insert(robotClickResult);
                }
                return;
            }
            //根据按钮+群发计划+电话号判断上行不存在,所以直接生成时间段的点击记录,会继续往下
            //todo 这儿有问题:如果没有这条上行,应该直接创建上行记录, 并且再判断是否有本时间段的点击记录robotClickResult,再决定是否创建此记录消息
            else
            {
                //新增操作
                if (btnType == 1){
                    robotClickResult.setBtnName("悬浮-"+buttonText);
                }else if (btnType == 2){
                    if (cardNum == -1){
                        robotClickResult.setBtnName(buttonText);
                    }else {
                        robotClickResult.setBtnName("卡片"+cardNum +"-"+buttonText);
                    }
                }
                robotClickResult.setClickAmount(1L);
                robotClickResult.setCreator(creator);
                robotClickResultMapper.insert(robotClickResult);
            }

            RobotPhoneUplinkResult uplinkResult = new RobotPhoneUplinkResult();
            //没有这条上行消息,新增
            uplinkResult.setPhoneNum(deliveryMessage.getSender())
                    .setBtnUuid(list.get(0))
                    .setMessageId(deliveryMessage.getMessageId())
                    .setReceiptTime(new Date())
                    .setActionType(deliveryMessage.getAction())
                    .setCreator(creator)
                    .setPlanDetailId(Long.valueOf(list.get(1)));
            uplinkResultMapper.insert(uplinkResult);


        } catch (ParseException e) {
            log.error("按钮回调失败",e);
            throw new BizException(SendGroupExp.SQL_ERROR);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread())
                lock.unlock();
        }

    }

    private Integer queryButtonType(String messageData) {
        RobotShortcutButtonResp robotShortcutButtonResp = robotProcessTreeApi.getRobotShortcutButtonResp(messageData);
        if(robotShortcutButtonResp != null){
            return robotShortcutButtonResp.getButtonType();
        }else{
            RobotProcessButtonResp robotProcessButtonResp = rebotSettingApi.getButtonByUuid(messageData);
            if(robotProcessButtonResp != null){
                return Integer.parseInt(robotProcessButtonResp.getButtonType());
            }
        }
        return null;
    }


}
