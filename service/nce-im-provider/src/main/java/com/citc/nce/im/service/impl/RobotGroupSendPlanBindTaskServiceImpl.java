package com.citc.nce.im.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.redis.config.RedisService;
import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.entity.RobotGroupSendPlanBindTaskDo;
import com.citc.nce.im.mapper.RobotGroupSendPlanBindTaskDao;
import com.citc.nce.im.mapper.RobotNodeResultMapper;
import com.citc.nce.im.msgenum.SupplierConstant;
import com.citc.nce.im.service.RobotGroupSendPlanBindTaskService;
import com.citc.nce.im.service.RobotGroupSendPlansDetailService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.mybatis.core.query.LambdaQueryWrapperX;
import com.citc.nce.robot.req.FontdoGroupSendResultReq;
import com.citc.nce.robot.req.RobotGroupSendPlansDetailReq;
import com.citc.nce.robot.vo.RobotGroupSendPlanBindTask;
import com.citc.nce.robot.vo.RobotGroupSendPlansDetail;
import com.citc.nce.tenant.MsgRecordApi;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.tenant.vo.req.UpdateByPhoneAndMessageIdReq;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import com.citc.nce.tenant.vo.resp.NodeResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RobotGroupSendPlanBindTaskServiceImpl implements RobotGroupSendPlanBindTaskService {

    @Resource
    RobotGroupSendPlanBindTaskDao sendPlanBindTaskDao;

    @Resource
    RobotNodeResultMapper robotNodeResultMapper;

    @Resource
    MsgRecordApi msgRecordApi;
    @Resource
    RobotGroupSendPlansDetailService robotGroupSendPlansDetailService;
    @Resource
    private BroadcastPlanService broadcastPlanService;
    @Resource
    private DeductionAndRefundApi deductionAndRefundApi;
    @Resource
    private AccountManagementApi accountManagementApi;
    @Resource
    private RedisService redisService;

    /**
     * 通过TaskId查询单条数据
     *
     * @param taskId 供应商会将TaskID
     * @return 实例对象
     */
    @Override
    public RobotGroupSendPlanBindTask queryByTaskId(String taskId, String appId) {
        RobotGroupSendPlanBindTask bind = new RobotGroupSendPlanBindTask();
        QueryWrapper<RobotGroupSendPlanBindTaskDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq("app_id", appId);
        RobotGroupSendPlanBindTaskDo robotGroupSendPlanDescDo = sendPlanBindTaskDao.selectOne(queryWrapper);
        for (int i = 0; i < 10; i++) {
            if (null != robotGroupSendPlanDescDo) {
                BeanUtils.copyProperties(robotGroupSendPlanDescDo, bind);
                return bind;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<RobotGroupSendPlanBindTask> queryByTaskIds(List<String> taskIds) {
        LambdaQueryWrapperX<RobotGroupSendPlanBindTaskDo> queryWrapper = new LambdaQueryWrapperX<>();
        //把数组变为列表
        queryWrapper.in(RobotGroupSendPlanBindTaskDo::getTaskId, taskIds);
        List<RobotGroupSendPlanBindTaskDo> robotGroupSendPlanDescDo = sendPlanBindTaskDao.selectList(queryWrapper);
        //做类型转换
        return BeanUtil.copyToList(robotGroupSendPlanDescDo, RobotGroupSendPlanBindTask.class);
    }

    /**
     * 通过TaskId查询单条数据, 此处是适配5G阅信的解析回执查询
     *
     * @param taskId 供应商会将TaskID
     * @return 实例对象
     */
    @Override
    public RobotGroupSendPlanBindTask queryByTaskId(String taskId) {
        RobotGroupSendPlanBindTask bind = new RobotGroupSendPlanBindTask();
        QueryWrapper<RobotGroupSendPlanBindTaskDo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        RobotGroupSendPlanBindTaskDo robotGroupSendPlanDescDo = sendPlanBindTaskDao.selectOne(queryWrapper);
        for (int i = 0; i < 2; i++) {
            if (null != robotGroupSendPlanDescDo) {
                BeanUtils.copyProperties(robotGroupSendPlanDescDo, bind);
                return bind;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    /*
     * @describe  对网关调用群发接口后的结果进行处理
     *  1.群发全部失败 :
     *      1.1 将此次发送所有的本地记录全部置为失败
     *      1.2 更新发送计划统一统计详细信息
     *  2.群发成功 :需要绑定taskId+旧MsgId+Appid  ,
     *      另外有旧MsgId与panDetailId早已经做了绑定, 见GroupNodeProcessor.sendToPlatform()
     * @Param
     * @param req
     * @param appid
     * @return int
     **/
    @Override
    public void bind(FontdoGroupSendResultReq req, String appid) {
        log.info("bind !supplierMessageResult req:{}, chatbotId:{}", req, appid);
        AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByAccountId(getRealChatbotId(appid));
        MsgRecordVo msgRecordVoUpdate = new MsgRecordVo();
        if (!SupplierConstant.SUCCESS_STATUS.equals(req.getCode())) {
            //群发全部失败
            msgRecordVoUpdate.setSendResult(DeliveryEnum.FAILED.getCode()).setReceiptTime(new Date());
            msgRecordVoUpdate.setFinalResult(RequestEnum.FAILED.getCode());
            //将此次发送所有的本地记录全部置为失败
            int updatedRecordNumber = updateFailMsgRecordInfo(req, accountManagementResp.getCustomerId(), msgRecordVoUpdate);
            //更新发送计划统一统计详细信息
            updateMassSendNodeStatisticInfo(req.getMsgId(), updatedRecordNumber, accountManagementResp.getCustomerId());
            //查找MsgRecord表, 如果是使用的余额付费, 需要修改扣费记录表中的行数据.
            //退还余额或者是套餐
            returnBalanceOfChargeConsumeRecord(req.getMsgId(), accountManagementResp.getCustomerId(), updatedRecordNumber);
        } else {
            //群发成功,需要绑定taskId+MsgId+Appid
            if (StringUtils.isNotEmpty(req.getMsgId())) {
                bindTaskIdAndMsgIdAndAppid(req.getMsgId(), req.getTaskId(), appid, accountManagementResp.getCustomerId());
            }
        }
    }

    private String getRealChatbotId(String chatbotId) {
        if (chatbotId.contains("sip:")) {
            chatbotId = chatbotId.replace("sip:", "");
            if (chatbotId.contains("@")) {
                String[] split1 = chatbotId.split("@");
                chatbotId = split1[0];
            }
        }
        return chatbotId;
    }

    private void returnBalanceOfChargeConsumeRecord(String messageId, String customerId, int updatedRecordNumber) {
        List<MsgRecordResp> msgRecordResps = msgRecordApi.queryRecordByMessageIdAndCustomerId(messageId, customerId);
        //防止msgRecord插入缓慢
        for (int i = 0; i < 5; i++) {
            if (CollectionUtil.isNotEmpty(msgRecordResps)) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            msgRecordResps = msgRecordApi.queryRecordByMessageIdAndCustomerId(messageId, customerId);
        }
        if (CollectionUtil.isEmpty(msgRecordResps)) {
            log.error("未找到对应的msgRecord记录, oldMessageId:{}, customerId:{}", messageId, customerId);
            return;
        }
        //是扣余额,需要处理余额扣费记录
        if (Objects.equals(PaymentTypeEnum.BALANCE.getCode(), msgRecordResps.get(0).getConsumeCategory())) {
            deductionAndRefundApi.returnBalanceBatchWithoutTariffType(ReturnBalanceBatchReq.builder()
                    .messageId(messageId)
                    .customerId(customerId)
                    .build());
        } else {
            broadcastPlanService.refundByDeliveryState(messageId, DeliveryEnum.FAILED, updatedRecordNumber, customerId);
        }
    }

    private void updateMassSendNodeStatisticInfo(String messageId, int updatedRecordNumber, String customerId) {
        if (StringUtils.isNotEmpty(messageId) && updatedRecordNumber > 0) {
            RobotGroupSendPlansDetailReq sendPlansDetailUpdate = queryMassSendNodeInfo(messageId, customerId);
            if (sendPlansDetailUpdate != null && sendPlansDetailUpdate.getUnknowAmount() != null && sendPlansDetailUpdate.getUnknowAmount() > 0) {
                //更新失败的数据
                sendPlansDetailUpdate.setFailAmount(sendPlansDetailUpdate.getFailAmount() + updatedRecordNumber);
                //更新未知的数据
                sendPlansDetailUpdate.setUnknowAmount(sendPlansDetailUpdate.getUnknowAmount() - updatedRecordNumber);
                robotGroupSendPlansDetailService.updateNodeDetail(sendPlansDetailUpdate);
            }
        }
    }

    /*
     * @describe 查到groupSendPlansDetail节点信息
     * @Param
     * @param messageId
     * @return com.citc.nce.robot.req.RobotGroupSendPlansDetailReq
     **/
    private RobotGroupSendPlansDetailReq queryMassSendNodeInfo(String messageId, String customerId) {
        RobotGroupSendPlansDetailReq groupSendPlansDetailUpdate = null;
        Long planDetailId = msgRecordApi.queryNodeIdByMessageId(messageId, customerId);
        if (planDetailId != null) {
            RobotGroupSendPlansDetail robotGroupSendPlansDetail = robotGroupSendPlansDetailService.queryById(planDetailId);
            groupSendPlansDetailUpdate = new RobotGroupSendPlansDetailReq();
            groupSendPlansDetailUpdate.setId(planDetailId);
            groupSendPlansDetailUpdate.setUnknowAmount(robotGroupSendPlansDetail.getUnknowAmount());
            groupSendPlansDetailUpdate.setFailAmount(robotGroupSendPlansDetail.getFailAmount());
            groupSendPlansDetailUpdate.setSuccessAmount(robotGroupSendPlansDetail.getSuccessAmount());
            groupSendPlansDetailUpdate.setSendAmount(robotGroupSendPlansDetail.getSendAmount());
        }
        return groupSendPlansDetailUpdate;
    }


    /*
     * @describe 网关调用蜂动的群发接口失败, 所有的本地储存的群发信息状态都改成失败
     * @Param
     * @param req
     * @param appid
     * @param msgRecordVoUpdate
     * @param code
     * @return int
     **/
    private int updateFailMsgRecordInfo(FontdoGroupSendResultReq req, String customerId, MsgRecordVo msgRecordVoUpdate) {
        if (StringUtils.isNotEmpty(req.getMsgId())) {
            UpdateByPhoneAndMessageIdReq update = new UpdateByPhoneAndMessageIdReq();
            update.setMsgType(MsgTypeEnum.M5G_MSG.getCode());
            update.setMessageId(req.getMsgId());
            update.setCustomerId(customerId);
            update.setMsgRecordVoUpdate(msgRecordVoUpdate);
            while (redisService.hasKey(String.format(req.getMsgId()))) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            return msgRecordApi.updateByPhoneAndMessageId(update);
        }
        return 0;
    }

    private void bindTaskIdAndMsgIdAndAppid(String msgId, String taskId, String appid, String customerId) {
        redisService.setCacheObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, taskId), taskId, 30L, TimeUnit.SECONDS);
        while (redisService.hasKey(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, msgId))) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        RobotGroupSendPlanBindTask bindTask = null;
        for (int i = 0; i < 3; i++) {
            try {
                //根据message_id查询nodeId和operatorCode
                NodeResp nodeResp = msgRecordApi.queryNodeByMessageId(msgId, customerId);
                Long planDetailId = nodeResp.getPlanDetailId();
                Integer operatorCode = nodeResp.getOperatorCode();
                log.error("planDetailId is null ,等待msgRecord插入");
                bindTask = new RobotGroupSendPlanBindTask();
                bindTask.setTaskId(taskId);
                bindTask.setPlanDetailId(planDetailId);
                //网关生成的旧消息Id,主要是锚定功能
                bindTask.setOldMessageId(msgId);
                bindTask.setAppId(appid);
                bindTask.setOperatorCode(operatorCode);
                bindTask.setCustomerId(nodeResp.getCreator());
                break;
            } catch (Exception e) {
                log.error("查询node信息失败,等待400ms");
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    log.error("bind sleep be broken 2");
                }
            }
        }

        //todo 查询阅信模板名称
        if (Objects.nonNull(bindTask)) {
            RobotGroupSendPlanBindTaskDo robotGroupSendPlanBindTaskDo = new RobotGroupSendPlanBindTaskDo();
            BeanUtils.copyProperties(bindTask, robotGroupSendPlanBindTaskDo);
            sendPlanBindTaskDao.insert(robotGroupSendPlanBindTaskDo);
        }
        redisService.deleteObject(String.format(Constants.MSG_RECORDS_INSERT_REDIS_KEY, taskId));
    }
}
