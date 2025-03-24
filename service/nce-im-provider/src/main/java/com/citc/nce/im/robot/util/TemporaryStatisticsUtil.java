package com.citc.nce.im.robot.util;

import com.citc.nce.common.thread.ThreadTaskUtils;
import com.citc.nce.im.robot.dto.message.MsgDto;
import com.citc.nce.im.robot.dto.robot.RobotDto;
import com.citc.nce.im.robot.enums.TemporaryStatisticsTypeEnum;
import com.citc.nce.im.util.ChannelTypeUtil;
import com.citc.nce.robot.TemporaryStatisticsApi;
import com.citc.nce.robot.vo.TemporaryStatisticsReq;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/7/25 17:44
 */
@Service
public class TemporaryStatisticsUtil {
    @Resource
    private TemporaryStatisticsApi temporaryStatisticsApi;

    private void saveTemporaryStatistics(int type, String customerId, String chatbotAccountId, String accountType, Long sceneId, Long processId) {
        TemporaryStatisticsReq temporaryStatisticsReq = new TemporaryStatisticsReq();
        if (null != sceneId) {
            temporaryStatisticsReq.setSceneId(sceneId);
        }
        if (null != processId) {
            temporaryStatisticsReq.setProcessId(processId);
        }
        temporaryStatisticsReq.setType(type);
        temporaryStatisticsReq.setCreator(customerId);
        temporaryStatisticsReq.setUpdater(customerId);
        temporaryStatisticsReq.setChatbotAccountId(chatbotAccountId);
        if (StringUtils.isNotEmpty(accountType)) {
            temporaryStatisticsReq.setChatbotType(ChannelTypeUtil.getChannelType(accountType));
        }
        Runnable runnable = () -> temporaryStatisticsApi.saveTemporaryStatisticsApi(temporaryStatisticsReq);
        ThreadTaskUtils.execute(runnable);
    }

    public void newConversation(MsgDto msgDto) {
        if(!Strings.isNullOrEmpty(msgDto.getPhone())){
            saveTemporaryStatistics(TemporaryStatisticsTypeEnum.CREATE_CONVERSATION.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), null, null);
        }
    }

    public void triggerProcess(MsgDto msgDto, Long sceneId, Long processId) {
        if(!Strings.isNullOrEmpty(msgDto.getPhone())){
            saveTemporaryStatistics(TemporaryStatisticsTypeEnum.TRIGGER_PROCESS.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), sceneId, processId);
        }
    }

    public void effectiveConversation(MsgDto msgDto, Long sceneId, Long processId) {
        if(!Strings.isNullOrEmpty(msgDto.getPhone())){
            saveTemporaryStatistics(TemporaryStatisticsTypeEnum.EFFECTIVE_CONVERSATION.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), sceneId, processId);
        }
    }

    public void completeProcess(MsgDto msgDto, String sceneId, String processId) {
        if(!Strings.isNullOrEmpty(msgDto.getPhone())){
            saveTemporaryStatistics(TemporaryStatisticsTypeEnum.COMPLETE_PROCESS.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), Long.parseLong(sceneId), Long.parseLong(processId));
        }
    }

    public void lastReply(MsgDto msgDto) {
        if(!Strings.isNullOrEmpty(msgDto.getPhone())){
            saveTemporaryStatistics(TemporaryStatisticsTypeEnum.LAST_REPLY.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), null, null);
        }
    }

    public void send(MsgDto msgDto) {
        saveTemporaryStatistics(TemporaryStatisticsTypeEnum.SEND.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), null, null);
    }
    public void receive(MsgDto msgDto, RobotDto robotDto) {
        saveTemporaryStatistics(TemporaryStatisticsTypeEnum.RECEIVE.getCode(), msgDto.getCustomerId(), msgDto.getChatbotAccountId(), msgDto.getAccountType(), Long.parseLong(robotDto.getSceneId()), Long.parseLong(robotDto.getCurrentProcessId()));

    }


}
