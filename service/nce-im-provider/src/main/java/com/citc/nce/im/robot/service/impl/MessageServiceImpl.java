package com.citc.nce.im.robot.service.impl;

import cn.hutool.core.util.IdUtil;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.chatbot.vo.ChatbotGetReq;
import com.citc.nce.auth.messagetemplate.MessageTemplateApi;
import com.citc.nce.auth.user.UserApi;
import com.citc.nce.auth.user.vo.resp.UserResp;
import com.citc.nce.im.robot.common.RobotConstant;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.service.MessageService;
import com.citc.nce.im.robot.util.RedisUtil;
import com.citc.nce.im.robot.util.RobotUtils;
import com.citc.nce.im.robot.util.TemporaryStatisticsUtil;
import com.citc.nce.im.session.processor.bizModel.RebotSettingModel;
import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.vo.RebotSettingQueryReq;
import com.citc.nce.robot.vo.RebotSettingResp;
import com.citc.nce.robot.vo.UpMsgReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/12 14:43
 */
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserApi userApi;
    @Resource
    private RebotSettingApi rebotSettingApi;
    @Resource
    private TemporaryStatisticsUtil temporaryStatisticsUtil;
    @Resource
    private AccountManagementApi accountManagementApi;

    @Override
    public MsgDto messageParse(UpMsgReq upMsgReq) {
        MsgDto res = new MsgDto();
        // 设置基本属性
        BeanUtils.copyProperties(upMsgReq, res);
        //设置res的MsgSourceType
        setMsgSource(upMsgReq, res);
        // 获取过期时间
        RebotSettingResp rebotSettingResp = getExpiredTime(res);
        Assert.notNull(rebotSettingResp,"机器人设置不能为空");
        // 调试窗口直接使用前端传递的会话Id
        // 如果入口是网关, 查找该用户(phone)与chatbot是否已经存在会话,有的话,重新设置该conversationId,没有的话表明是新会话
        setConversationId(upMsgReq, res, rebotSettingResp);
        String robotKey = RedisUtil.getRobotKeyByConversation(res.getConversationId());
        // 是否存过机器人设置
        setRobotDto(res, rebotSettingResp, robotKey);
        //这里需要将supplier_tag标志加入,以判断是否是供应商机器人
        setsupplierTag(upMsgReq,res);

        return res;
    }

    //设置supplierTag
    private void setsupplierTag(UpMsgReq upMsgReq, MsgDto res) {
        ChatbotGetReq chatbotGetReq = new ChatbotGetReq();
        //uuid
        AccountManagementResp accountManagement = accountManagementApi.getAccountManagementByAccountId(upMsgReq.getChatbotAccount());
        if(null != accountManagement){
            res.setSupplierTag(accountManagement.getSupplierTag());
            res.setOperator(accountManagement.getAccountTypeCode());
        }
    }

    private void setRobotDto(MsgDto res, RebotSettingResp rebotSettingResp, String robotKey) {
        String robot = stringRedisTemplate.opsForValue().get(robotKey);
        if (StringUtils.isEmpty(robot)) {
            // 保存机器人设置
            RobotDto robotDto = new RobotDto();
            robotDto.setExpireTime(Long.parseLong(String.valueOf(rebotSettingResp.getWaitTime())));
            robotDto.setConversationId(res.getConversationId());
            RebotSettingModel rebotSettingModel = new RebotSettingModel();
            BeanUtils.copyProperties(rebotSettingResp, rebotSettingModel);
            robotDto.setRebotSettingModel(rebotSettingModel);
            RobotUtils.saveRobot(robotDto);
        }
    }

    /*
     * @describe 查找该用户(phone)对该chatbot是否已经存在会话,
     *          有的话,重新设置该conversationId,没有的话表明是新会话
     * @Param
     * @param upMsgReq
     * @param res
     * @param rebotSettingResp
     * @return void
     **/
    private void setConversationId(UpMsgReq upMsgReq, MsgDto res, RebotSettingResp rebotSettingResp) {
        if (StringUtils.equals(RobotConstant.MSG_SOURCE_GATEWAY, upMsgReq.getMessageSource())) {
            // 生成会话id
            String getConversationKeyByPhone = RedisUtil.getConversationKeyByPhone(res.getPhone(), res.getChatbotAccount());
            String conversationId = stringRedisTemplate.opsForValue().get(getConversationKeyByPhone);
            if (StringUtils.isEmpty(res.getCreate())) {
                // 获取用户
                UserResp userResp = userApi.findByPhone(res.getPhone());
                res.setCreate(userResp.getUserId());
            }
            if (StringUtils.isNotEmpty(conversationId)) {
                res.setConversationId(conversationId);
            } else {
                String newConversationId = upMsgReq.getConversationId();
                res.setConversationId(newConversationId);
                // 统计-新会话
                temporaryStatisticsUtil.newConversation(res);
                // 设置过期时间
                stringRedisTemplate.opsForValue().set(getConversationKeyByPhone, newConversationId, rebotSettingResp.getWaitTime(), TimeUnit.MINUTES);
            }
        }
    }

    private static void setMsgSource(UpMsgReq upMsgReq, MsgDto res) {
        if (null != upMsgReq.getFalg()) {
            res.setMessageSource(String.valueOf(upMsgReq.getFalg()));
        } else {
            res.setMessageSource(RobotConstant.MSG_SOURCE_DEBUG);
        }
    }

    private RebotSettingResp getExpiredTime(MsgDto res) {
        RebotSettingQueryReq rebotSettingQueryReq = new RebotSettingQueryReq();
        rebotSettingQueryReq.setCreate(res.getCustomerId());
        RebotSettingResp rebotSettingResp = rebotSettingApi.getRebotSettingReq(rebotSettingQueryReq);
        if(null != rebotSettingResp && null == rebotSettingResp.getWaitTime()){
            rebotSettingResp.setWaitTime(5);
        }
        return rebotSettingResp;
    }
}
