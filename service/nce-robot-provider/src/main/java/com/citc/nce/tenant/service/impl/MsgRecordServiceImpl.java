package com.citc.nce.tenant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementTypeReq;
import com.citc.nce.auth.common.CSPChatbotSupplierTagEnum;
import com.citc.nce.auth.csp.recharge.Const.PaymentTypeEnum;
import com.citc.nce.auth.prepayment.DeductionAndRefundApi;
import com.citc.nce.auth.prepayment.PrepaymentApi;
import com.citc.nce.auth.prepayment.vo.ReturnBalanceBatchReq;
import com.citc.nce.common.core.enums.MsgSubTypeEnum;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.messsagecenter.service.MessageCenterService;
import com.citc.nce.msgenum.DeliveryEnum;
import com.citc.nce.msgenum.RequestEnum;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.enums.MessageType;
import com.citc.nce.robot.exception.MsgErrorCode;
import com.citc.nce.robot.vo.SendMessageNumberDetail;
import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import com.citc.nce.tenant.robot.dao.MsgIdMappingDao;
import com.citc.nce.tenant.robot.dao.MsgRecordDao;
import com.citc.nce.tenant.robot.dao.RobotPhoneUplinkResult1Dao;
import com.citc.nce.tenant.robot.entity.MsgIdMappingDo;
import com.citc.nce.tenant.robot.entity.MsgRecordDo;
import com.citc.nce.tenant.robot.entity.RobotPhoneUplinkResult;
import com.citc.nce.tenant.service.MsgRecordService;
import com.citc.nce.tenant.vo.req.*;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import com.citc.nce.tenant.vo.resp.MsgSendDetailResultResp;
import com.citc.nce.tenant.vo.resp.NodeResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.citc.nce.robot.enums.MessageType.LOCATION;
import static com.citc.nce.robot.enums.MessageType.TEXT;

@Service
@Slf4j
public class MsgRecordServiceImpl implements MsgRecordService {
    @Resource
    private MsgRecordDao msgRecordDao;
    @Resource
    private MsgIdMappingDao msgIdMappingDao;
    @Resource
    private RobotPhoneUplinkResult1Dao robotPhoneUplinkResult1Dao;

    @Resource
    private AccountManagementApi accountManagementApi;

    @Resource
    private MessageCenterService messageCenterService;

    @Value("${userId.superAdministrator}")
    private String superAdministrator;

    @Resource
    private PrepaymentApi prepaymentApi;

    @Resource
    private DeductionAndRefundApi deductionAndRefundApi;

    @Override
    public void insert(MsgRecordVo req) {
        MsgRecordDo msgRecordDo = new MsgRecordDo();
        BeanUtils.copyProperties(req, msgRecordDo);
        msgRecordDao.insert(msgRecordDo);
    }

    @Override
    public List<String> queryPhoneList(Long planDetailId, int accountType, List<Integer> msgStatusList, String date) {
        LambdaQueryWrapper<MsgRecordDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MsgRecordDo::getAccountType, accountType);
        queryWrapper.eq(MsgRecordDo::getPlanDetailId, planDetailId);
        BaseUser user = SessionContextUtil.getUser();
        if (user != null)
            queryWrapper.eq(BaseDo::getCreator, user.getUserId());
        queryWrapper.in(MsgRecordDo::getSendResult, msgStatusList);
        queryWrapper.apply("UNIX_TIMESTAMP(receipt_time) <= UNIX_TIMESTAMP('" + date + "')");
        List<MsgRecordDo> robotPhoneResults = msgRecordDao.selectList(queryWrapper);
        return robotPhoneResults.stream().map(MsgRecordDo::getPhoneNum).distinct().collect(Collectors.toList());
    }

    /*
    SELECT a.phone_num
        FROM robot_phone_result a
        WHERE a.phone_num NOT IN
              (SELECT DISTINCT phone_num
               FROM robot_phone_result
               WHERE message_id = #{messageId} AND send_result = 6)
          AND a.message_id = #{messageId}
          AND a.send_result = 1 AND UNIX_TIMESTAMP(receipt_time) &lt;= UNIX_TIMESTAMP(#{nowStr});
     */
    @Override
    public List<String> queryUnReadFor5G(Long nodeId, String date) {
        QueryWrapper<MsgRecordDo> msgRecordDoQueryWrapper = new QueryWrapper<>();
//        msgRecordDoQueryWrapper.ne("send_result", 6);
        msgRecordDoQueryWrapper.eq("plan_detail_id", nodeId);
        msgRecordDoQueryWrapper.eq("send_result", 1);
        msgRecordDoQueryWrapper.apply("UNIX_TIMESTAMP(receipt_time) <= UNIX_TIMESTAMP('" + date + "')");
        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(msgRecordDoQueryWrapper);
        return msgRecordDos.stream().map(MsgRecordDo::getPhoneNum).distinct().collect(Collectors.toList());
    }


    /*
    SELECT phone_num FROM robot_phone_result WHERE message_id = #{messageId}
        AND phone_num NOT IN (
        SELECT DISTINCT phone_num FROM  robot_phone_uplink_result WHERE plan_detail_id = #{detailId}  AND btn_uuid IS NOT null
        )
        AND send_result IN (1,6) and UNIX_TIMESTAMP(receipt_time) &lt;= UNIX_TIMESTAMP(#{nowStr});
     */
    @Override
    public List<String> queryNotClick(String date, Long parentNodeId) {
        QueryWrapper<RobotPhoneUplinkResult> robotPhoneUplinkResultLambdaQueryWrapper = new QueryWrapper<>();
        robotPhoneUplinkResultLambdaQueryWrapper.select("phone_num as phone");
        robotPhoneUplinkResultLambdaQueryWrapper.eq("plan_detail_id", parentNodeId);
        robotPhoneUplinkResultLambdaQueryWrapper.isNotNull("btn_uuid");
        robotPhoneUplinkResultLambdaQueryWrapper.groupBy("phone");
        List<Map<String, Object>> resultMaps = robotPhoneUplinkResult1Dao.selectMaps(robotPhoneUplinkResultLambdaQueryWrapper);
        List<String> phones = new ArrayList<>();
        for (Map<String, Object> item : resultMaps) {
            phones.add(item.get("phone").toString());
        }
        QueryWrapper<MsgRecordDo> msgRecordDoQueryWrapper2 = new QueryWrapper<>();
        msgRecordDoQueryWrapper2.eq("plan_detail_id", parentNodeId);
        msgRecordDoQueryWrapper2.notIn(!phones.isEmpty(), "phone_num", phones);
        msgRecordDoQueryWrapper2.in("send_result", Arrays.asList(1, 6));
        msgRecordDoQueryWrapper2.apply("UNIX_TIMESTAMP(receipt_time) <= UNIX_TIMESTAMP('" + date + "')");
        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(msgRecordDoQueryWrapper2);
        return msgRecordDos.stream().map(MsgRecordDo::getPhoneNum).distinct().collect(Collectors.toList());
    }

    @Override
    public void insertBatch(List<MsgRecordVo> msgRecordReqs) {
        if (!CollectionUtils.isEmpty(msgRecordReqs)) {
            MsgRecordDo msgRecordDo;
            List<MsgRecordDo> msgRecordDos = new ArrayList<>();
            for (MsgRecordVo item : msgRecordReqs) {
                msgRecordDo = new MsgRecordDo();
                BeanUtils.copyProperties(item, msgRecordDo);
                msgRecordDos.add(msgRecordDo);
            }
            msgRecordDao.insertBatch(msgRecordDos);
        }
    }

    /*
    SELECT send_result,COUNT(id) num FROM robot_phone_result  WHERE message_id = #{messageId} in ('sent','failed') GROUP BY send_result
     */
    @Override
    public Map<Integer, Long> countByMsgStatus(Long nodeId) {
        Map<Integer, Long> countByMsgStatusMap = new HashMap<>();
        QueryWrapper<MsgRecordDo> msgRecordDoQueryWrapper = new QueryWrapper<>();
        msgRecordDoQueryWrapper.select("send_result AS sendResult,COUNT(id) num");
        msgRecordDoQueryWrapper.eq("plan_detail_id", nodeId);
        msgRecordDoQueryWrapper.groupBy("sendResult");
        List<Map<String, Object>> resultMaps = msgRecordDao.selectMaps(msgRecordDoQueryWrapper);
        if (!CollectionUtils.isEmpty(resultMaps)) {
            for (Map<String, Object> item : resultMaps) {
                countByMsgStatusMap.put((Integer) item.get("sendResult"), (Long) item.get("num"));
            }
        }
        return countByMsgStatusMap;
    }


    @Override
    public Integer updateByPhoneAndMessageId(UpdateByPhoneAndMessageIdReq req) {
        LambdaUpdateWrapper<MsgRecordDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MsgRecordDo::getAccountType, req.getMsgType());
        wrapper.eq(req.getPhoneNum() != null, MsgRecordDo::getPhoneNum, req.getPhoneNum());
        wrapper.eq(MsgRecordDo::getMessageId, req.getMessageId());
        wrapper.eq(req.getCustomerId() != null, MsgRecordDo::getCreator, req.getCustomerId());
        MsgRecordVo msgRecordVoUpdate = req.getMsgRecordVoUpdate();
        if (msgRecordVoUpdate != null) {
            if (msgRecordVoUpdate.getSendResult() != null) {
                wrapper.set(MsgRecordDo::getSendResult, msgRecordVoUpdate.getSendResult());
            }
            if (msgRecordVoUpdate.getReceiptTime() != null) {
                wrapper.set(MsgRecordDo::getReceiptTime, msgRecordVoUpdate.getReceiptTime());
            }
            if (msgRecordVoUpdate.getFinalResult() != null) {
                wrapper.set(MsgRecordDo::getFinalResult, msgRecordVoUpdate.getFinalResult());
            }
            if (msgRecordVoUpdate.getReadTime() != null) {
                wrapper.set(MsgRecordDo::getReadTime, msgRecordVoUpdate.getReadTime());
            }
            if (msgRecordVoUpdate.getFailedReason() != null) {
                wrapper.set(MsgRecordDo::getFailedReason, msgRecordVoUpdate.getFailedReason());
            }
            return msgRecordDao.update(new MsgRecordDo(), wrapper);
        }
        return 0;
    }

    @Override
    public MsgRecordVo selectByPhoneAndMessageId(Integer msgType, String phoneNum, String messageId, String customerId) {

        LambdaQueryWrapper<MsgRecordDo> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(MsgRecordDo::getAccountType, msgType);
        updateWrapper.eq(MsgRecordDo::getPhoneNum, phoneNum);
        updateWrapper.eq(StrUtil.isNotBlank(customerId), MsgRecordDo::getCreator, customerId);
        updateWrapper.eq(MsgRecordDo::getMessageId, messageId);
        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(updateWrapper);
        if (CollectionUtils.isEmpty(msgRecordDos)) {
            return null;
        }
        MsgRecordVo msgRecordVo = new MsgRecordVo();
        MsgRecordDo msgRecordDo = msgRecordDos.get(0);
        BeanUtils.copyProperties(msgRecordDo, msgRecordVo);
        return msgRecordVo;
    }

    @Override
    public void updateMessageId(Integer msgType, String oldMessageId, String messageId, String customerId) {
        LambdaUpdateWrapper<MsgRecordDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MsgRecordDo::getAccountType, msgType);
        wrapper.eq(MsgRecordDo::getMessageId, oldMessageId);
        wrapper.eq(StrUtil.isNotBlank(customerId), MsgRecordDo::getCreator, customerId);
        wrapper.set(MsgRecordDo::getMessageId, messageId);
        msgRecordDao.update(null, wrapper);

        List<MsgIdMappingDo> inserts = CollectionUtil.newArrayList();
        Set<String> platformMsgIds = CollectionUtil.newHashSet(messageId);
        for (String platformMsgId : platformMsgIds) {
            inserts.add(new MsgIdMappingDo()
                    .setMessageId(oldMessageId)
                    .setPlatformMsgId(platformMsgId)
                    .setCustomerId(customerId));
        }
        msgIdMappingDao.insertBatch(inserts);
    }

    @Override
    public long selectCountByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String messageId) {
        LambdaQueryWrapper<MsgRecordDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MsgRecordDo::getPlanDetailId, planDetailId);
        queryWrapper.eq(MsgRecordDo::getAccountType, msgType);
        queryWrapper.eq(MsgRecordDo::getMessageId, messageId);
        return msgRecordDao.selectCount(queryWrapper);

    }

    @Override
    public void updateReceiptTimeByByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String
            messageId, Date receiptTime) {
        LambdaUpdateWrapper<MsgRecordDo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MsgRecordDo::getAccountType, msgType);
        wrapper.eq(MsgRecordDo::getPlanDetailId, planDetailId);
        wrapper.eq(MsgRecordDo::getMessageId, messageId);
        wrapper.set(MsgRecordDo::getReceiptTime, receiptTime);
        msgRecordDao.update(null, wrapper);
    }

    @Override
    public PageResult<MsgSendDetailResultResp> selectSendDetail(MsgSendDetailReq queryReq) {
        PageResult<MsgSendDetailResultResp> pageResult = new PageResult<>();
        if (StringUtils.isEmpty(queryReq.getStartTime()) || StringUtils.isEmpty(queryReq.getEndTime())) {
            pageResult.setList(new ArrayList<>());
            pageResult.setTotal(0L);
            return pageResult;
        }

        LambdaUpdateWrapper<MsgRecordDo> wrapper = new LambdaUpdateWrapper<>();
        conditionalFilter(queryReq, wrapper);
        wrapper.orderByDesc(MsgRecordDo::getSendTime);
        Page<MsgRecordDo> page = new Page<>(queryReq.getPageNo(), queryReq.getPageSize());
        Page<MsgRecordDo> selectPage = msgRecordDao.selectPage(page, wrapper);
        List<MsgSendDetailResultResp> resultList = new ArrayList<>();

        selectPage.getRecords().forEach(r -> {
            MsgSendDetailResultResp resultResp = new MsgSendDetailResultResp();
            BeanUtils.copyProperties(r, resultResp);
            resultResp.setCallerAccountName(r.getAccountName());
            resultResp.setSignature(r.getSign());
            if (r.getSendResult() == DeliveryEnum.DELIVERED.getCode() || r.getSendResult() == DeliveryEnum.DISPLAYED.getCode()) {
                resultResp.setSendResult(RequestEnum.SUCCESS.getCode());
            }
            resultList.add(resultResp);
        });

        pageResult.setTotal(selectPage.getTotal());
        pageResult.setList(resultList);
        return pageResult;
    }

    private void conditionalFilter(MsgSendDetailReq queryReq, LambdaUpdateWrapper<MsgRecordDo> wrapper) {
        try {
            //发送结果
            if (ObjectUtil.isNotEmpty(queryReq.getFinalResult())) {
                //筛选发送成功的
                switch (queryReq.getFinalResult()) {
                    case 1:
                        //成功
                        wrapper.in(MsgRecordDo::getFinalResult, RequestEnum.SUCCESS.getCode());
                        break;
                    case 2:
                        //失败
                        wrapper.eq(MsgRecordDo::getFinalResult, RequestEnum.FAILED.getCode());
                        break;
                    case 3:
                        //已撤回
                        wrapper.eq(MsgRecordDo::getFinalResult, RequestEnum.REVOKE_SUCCESS.getCode());
                        break;
                    case 4:
                        // 回落短信
                        wrapper.eq(MsgRecordDo::getFinalResult, RequestEnum.FALLBACK_SMS.getCode());
                        break;
                    case 0:
                        //未知
                        wrapper.eq(MsgRecordDo::getFinalResult, RequestEnum.UN_KNOW.getCode());
                        break;
                    default:
                        break;
                }
            }
            wrapper.eq(MsgRecordDo::getAccountType, queryReq.getAccountType());
            //起始时间
            if (StringUtils.isNotEmpty(queryReq.getStartTime()) && StringUtils.isNotEmpty(queryReq.getEndTime())) {
                Date startTime = com.citc.nce.robot.util.DateUtil.stringToDate(queryReq.getStartTime(), "yyyy-MM-dd");
                if (null == startTime) {
                    throw new BizException("时间参数错误");
                }
                Date endaTime = com.citc.nce.robot.util.DateUtil.stringToDate(queryReq.getEndTime(), "yyyy-MM-dd");
                if (null == endaTime) {
                    throw new BizException("时间参数错误");
                }
                //后一天的开始时间作为结束时间，查询时不包括
                endaTime = DateUtil.beginOfDay(DateUtil.offsetDay(endaTime, 1));
                wrapper.ge(MsgRecordDo::getSendTime, startTime);
                wrapper.lt(MsgRecordDo::getSendTime, endaTime);
            } else {
                wrapper.ge(MsgRecordDo::getSendTime, DateUtil.offsetMonth(new Date(), -6));
            }
            // 回执时间
            if (StringUtils.isNotEmpty(queryReq.getReceiptStartTime()) && StringUtils.isNotEmpty(queryReq.getReceiptEndTime())) {
                Date startTime = com.citc.nce.robot.util.DateUtil.stringToDate(queryReq.getReceiptStartTime(), "yyyy-MM-dd");
                if (null == startTime) {
                    throw new BizException("时间参数错误");
                }
                Date endaTime = com.citc.nce.robot.util.DateUtil.stringToDate(queryReq.getReceiptEndTime(), "yyyy-MM-dd");
                if (null == endaTime) {
                    throw new BizException("时间参数错误");
                }
                //后一天的开始时间作为结束时间，查询时不包括
                endaTime = DateUtil.beginOfDay(DateUtil.offsetDay(endaTime, 1));
                wrapper.ge(MsgRecordDo::getReceiptTime, startTime);
                wrapper.lt(MsgRecordDo::getReceiptTime, endaTime);
            } else {
                wrapper.ge(MsgRecordDo::getReceiptTime, DateUtil.offsetMonth(new Date(), -6));
            }
            //账号
            if (StringUtils.isNotEmpty(queryReq.getCallerAccount())) {
                if (MsgTypeEnum.M5G_MSG.getCode() == queryReq.getAccountType()) {
                    if ("-1".equals(queryReq.getCallerAccount())) {
                        wrapper.eq(MsgRecordDo::getAccountName, "");
                    } else {
                        wrapper.eq(MsgRecordDo::getCallerAccount, queryReq.getCallerAccount());
                    }
                } else {
                    wrapper.like(MsgRecordDo::getCallerAccount, queryReq.getCallerAccount());
                }
            }
            //消息来源
            if (ObjectUtil.isNotEmpty(queryReq.getMessageResource())) {
                wrapper.eq(MsgRecordDo::getMessageResource, queryReq.getMessageResource());
            }
            //手机号
            if (StringUtils.isNotEmpty(queryReq.getPhoneNum())) {
                wrapper.like(MsgRecordDo::getPhoneNum, queryReq.getPhoneNum());
            }
            //消息内容
            if (MsgTypeEnum.M5G_MSG.getCode() != queryReq.getAccountType() && (StringUtils.isNotEmpty(queryReq.getMessageContent()))) {
                wrapper.like(MsgRecordDo::getMessageContent, queryReq.getMessageContent());
            }
            String userId = SessionContextUtil.getUser().getUserId();
            if (!StringUtils.equals(userId, superAdministrator)) {
                wrapper.eq(MsgRecordDo::getCreator, userId);
            }
        } catch (Exception e) {
            throw new BizException(MsgErrorCode.TIME_FORMAT_ERROR);
        }

    }


    @Override
    @Transactional
    @ShardingSphereTransactionType(TransactionType.BASE)
    public Boolean withdraw(MsgWithdrawIdReq msgWithdrawIdReq) {
        LambdaQueryWrapper<MsgRecordDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgRecordDo::getId, msgWithdrawIdReq.getId());
        wrapper.eq(MsgRecordDo::getCreator, SessionContextUtil.getUser().getUserId());
        MsgRecordDo msgRecordDo = msgRecordDao.selectOne(wrapper);
        if (Objects.isNull(msgRecordDo)) {
            throw new BizException("要撤回的数据不存在");
        }
        AccountManagementTypeReq typeReq = new AccountManagementTypeReq();
        typeReq.setCreator(SessionContextUtil.getUser().getUserId());
        typeReq.setAccountType(msgRecordDo.getCallerAccount());
        AccountManagementResp account = accountManagementApi.getAccountManagementByAccountType(typeReq);
        if (Objects.equals(CSPChatbotSupplierTagEnum.FONTDO.getValue(), account.getSupplierTag()))
            throw new BizException("账号暂不支持撤回功能");
        return messageCenterService.withdraw(msgRecordDo, account);
    }

    @Override
    public Long queryNodeIdByMessageId(String messageId, String customerId) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .eq(StrUtil.isNotBlank(customerId), MsgRecordDo::getCreator, customerId)
                .last("limit 1");

        MsgRecordDo msgRecordDo = msgRecordDao.selectOne(qw);
        if (msgRecordDo != null) {
            if (msgRecordDo.getMessageResource() != null && MessageResourceType.FAST_GROUP.getCode() == msgRecordDo.getMessageResource()) {//如果是快捷群发，则返回plan_id
                return msgRecordDo.getPlanId();
            } else {
                return msgRecordDo.getPlanDetailId();
            }
        }
        return null;
    }

    @Override
    public NodeResp queryNodeByMessageId(String messageId, String customerId) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .eq(StrUtil.isNotBlank(customerId), MsgRecordDo::getCreator, customerId)
                .select(MsgRecordDo::getPlanDetailId, MsgRecordDo::getCreator, MsgRecordDo::getOperatorCode)
                .last("limit 1");
        MsgRecordDo msgRecordDo = msgRecordDao.selectOne(qw);
        NodeResp nodeResp = new NodeResp();
        if (Objects.isNull(msgRecordDo)) {
            throw new BizException("消息不存在");
        }
        BeanUtil.copyProperties(msgRecordDo, nodeResp);
        return nodeResp;
    }

    @Override
    public List<SimpleMessageSendNumberDetail> queryFallbackMessageSendNumberDetail(String
                                                                                            customerId, LocalDateTime start, LocalDateTime end) {
        return msgRecordDao.count5gFallbackSend(customerId, start, end);
    }

    @Override
    public String queryAccountIdByMessageId(String messageId) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .select(MsgRecordDo::getAccountId)
                .last("limit 1");
        return Optional.ofNullable(msgRecordDao.selectOne(qw))
                .map(MsgRecordDo::getAccountId)
                .orElse(null);
    }

    @Override
    public MsgRecordResp queryRecordByMessageId(String messageId) {
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .last("limit 1");
        return Optional.ofNullable(msgRecordDao.selectOne(qw))
                .map(msgRecordDo -> {
                    MsgRecordResp resp = new MsgRecordResp();
                    resp.setMessageId(msgRecordDo.getMessageId());
                    resp.setMessageResource(msgRecordDo.getMessageResource());
                    resp.setMessageType(msgRecordDo.getMessageType());
                    resp.setAccountId(msgRecordDo.getAccountId());
                    resp.setTemplateId(msgRecordDo.getTemplateId());
                    resp.setCustomerId(msgRecordDo.getCreator());
                    resp.setPlanId(msgRecordDo.getPlanId());
                    resp.setPlanDetailId(msgRecordDo.getPlanDetailId());
                    resp.setConversationId(msgRecordDo.getConversationId());
                    return resp;
                })
                .orElse(null);
    }

    @Override
    public MsgRecordResp queryRecordByMessageIdAndPhoneNumber(String messageId, String phoneNumber, String customerId) {
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .eq(MsgRecordDo::getPhoneNum, phoneNumber)
                .eq(MsgRecordDo::getCreator, customerId)
                .last("limit 1");
        return Optional.ofNullable(msgRecordDao.selectOne(qw))
                .map(msgRecordDo -> {
                    MsgRecordResp resp = new MsgRecordResp();
                    resp.setMessageId(msgRecordDo.getMessageId());
                    resp.setMessageResource(msgRecordDo.getMessageResource());
                    resp.setMessageType(msgRecordDo.getMessageType());
                    resp.setAccountId(msgRecordDo.getAccountId());
                    resp.setTemplateId(msgRecordDo.getTemplateId());
                    resp.setPhoneNum(msgRecordDo.getPhoneNum());
                    resp.setCustomerId(msgRecordDo.getCreator());
                    resp.setPlanDetailId(msgRecordDo.getPlanDetailId());
                    resp.setConversationId(msgRecordDo.getConversationId());
                    resp.setConsumeCategory(msgRecordDo.getConsumeCategory());
                    return resp;
                })
                .orElse(null);
    }

    @Override
    public List<MsgRecordResp> queryRecordByMessageIdAndCustomerId(String messageId, String customerId) {
        List<MsgRecordResp> result = CollectionUtil.newArrayList();

        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, messageId)
                .eq(MsgRecordDo::getCreator, customerId);
        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(qw);
        //批量转换
        if (CollectionUtil.isEmpty(msgRecordDos)) {
            return result;
        }
        for (MsgRecordDo msgRecordDo : msgRecordDos) {
            MsgRecordResp resp = new MsgRecordResp();
            resp.setMessageId(msgRecordDo.getMessageId());
            resp.setMessageResource(msgRecordDo.getMessageResource());
            resp.setMessageType(msgRecordDo.getMessageType());
            resp.setAccountId(msgRecordDo.getAccountId());
            resp.setTemplateId(msgRecordDo.getTemplateId());
            resp.setCustomerId(msgRecordDo.getCreator());
            resp.setPlanDetailId(msgRecordDo.getPlanDetailId());
            resp.setConversationId(msgRecordDo.getConversationId());
            resp.setConsumeCategory(msgRecordDo.getConsumeCategory());
            resp.setPhoneNum(msgRecordDo.getPhoneNum());
            result.add(resp);
        }
        return result;
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getCreator, customerId)
                .eq(MsgRecordDo::getAccountType, msgType.getCode())
                .eq(MsgRecordDo::getAccountId, accountId);
        return msgRecordDao.exists(qw);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndBetweenDate(String customerId, MsgTypeEnum msgType, LocalDateTime
            start, LocalDateTime end) {
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getCreator, customerId)
                .eq(MsgRecordDo::getAccountType, msgType.getCode())
                .gt(BaseDo::getCreateTime, Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .lt(BaseDo::getCreateTime, Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        return msgRecordDao.exists(qw);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndAccountIdAndBetweenDate(String accountId, MsgTypeEnum msgType, LocalDateTime
            start, LocalDateTime end) {
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getAccountId, accountId)
                .eq(MsgRecordDo::getAccountType, msgType.getCode())
                .gt(BaseDo::getCreateTime, Date.from(start.atZone(ZoneId.systemDefault()).toInstant()))
                .lt(BaseDo::getCreateTime, Date.from(end.atZone(ZoneId.systemDefault()).toInstant()));
        return msgRecordDao.exists(qw);
    }

    /**
     * 查询客户消息发送数量详情
     *
     * @param customerId 客户id
     * @param start      起始时间
     * @param end        结束时间
     * @return
     */
    @Override
    public SendMessageNumberDetail querySendNumberDetail(String customerId, LocalDateTime start, LocalDateTime end) {
        SendMessageNumberDetail detail = new SendMessageNumberDetail();
        detail.setSmsUsage(msgRecordDao.countSmsSend(customerId, start, end));
        detail.setMediaUsage(msgRecordDao.countVideoSend(customerId, start, end));
        detail.set_5gTextUsage(msgRecordDao.count5gTextSend(customerId, start, end));
        detail.set_5gRichUsage(msgRecordDao.count5gRichSend(customerId, start, end));
        detail.set_5gConversationUsage(msgRecordDao.countConversationSend(customerId, start, end));
        detail.set_5gFallbackUsage(msgRecordDao.count5gFallbackSend(customerId, start, end));
        return detail;
    }


    @Override
    public Map<MsgTypeEnum, List<MessageResourceType>> querySendMsgTypeListBetween(String customerId, LocalDateTime
            start, LocalDateTime end) {
        return msgRecordDao.querySendAccountTypeListBetween(customerId, start, end)
                .stream()
                .collect(
                        Collectors.groupingBy(
                                dto -> MsgTypeEnum.getValue(dto.getAccountType()),
                                HashMap::new,
                                Collectors.mapping(dto -> MessageResourceType.fromCode(dto.getMessageSource()), Collectors.toList())
                        )
                );
    }

    @Override
    public List<MsgRecordVo> selectSendDetailByPlanDetailIds(List<Long> planDetailIds) {
        List<MsgRecordVo> recordVos = new ArrayList<>();
        LambdaQueryWrapper<MsgRecordDo> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.in(MsgRecordDo::getPlanDetailId, planDetailIds);
        List<MsgRecordDo> msgRecordDos = msgRecordDao.selectList(updateWrapper);
        if (!CollectionUtils.isEmpty(msgRecordDos)) {
            MsgRecordVo msgRecordVo;
            for (MsgRecordDo item : msgRecordDos) {
                msgRecordVo = new MsgRecordVo();
                BeanUtils.copyProperties(item, msgRecordVo);
                recordVos.add(msgRecordVo);
            }
        }
        return recordVos;
    }

    @Override
    public void insertMsgIdMapping(MsgIdMappingVo req) {

        List<MsgIdMappingDo> inserts = CollectionUtil.newArrayList();
        Set<String> platformMsgIds = req.getPlatformMsgIds();
        if (!CollectionUtil.isEmpty(platformMsgIds)) {
            for (String platformMsgId : platformMsgIds) {
                inserts.add(new MsgIdMappingDo()
                        .setMessageId(req.getMessageId())
                        .setPlatformMsgId(platformMsgId)
                        .setCustomerId(req.getCustomerId()));
            }
            msgIdMappingDao.insertBatch(inserts);
        }
    }

    @Override
    public String queryMsgIdMapping(String platformMsgId) {
        LambdaQueryWrapper<MsgIdMappingDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MsgIdMappingDo::getPlatformMsgId, platformMsgId);
        wrapper.orderByDesc(MsgIdMappingDo::getCreateTime);
        wrapper.last("limit 1");
        return Optional.ofNullable(msgIdMappingDao.selectOne(wrapper))
                .map(MsgIdMappingDo::getMessageId)
                .orElseThrow(() -> new BizException(String.format("未能查询到平台消息id：%s 对应的message id", platformMsgId)));
    }

    @Override
    public MsgIdMappingVo queryMsgMapping(String platformMsgId) {
        LambdaQueryWrapper<MsgIdMappingDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MsgIdMappingDo::getPlatformMsgId, platformMsgId);
        wrapper.orderByDesc(MsgIdMappingDo::getCreateTime);
        wrapper.last("limit 1");
        MsgIdMappingDo msgIdMappingDo = msgIdMappingDao.selectOne(wrapper);
        if (msgIdMappingDo == null) {
            return null;
        }
        MsgIdMappingVo msgIdMappingVo = new MsgIdMappingVo();
        msgIdMappingVo.setCustomerId(msgIdMappingDo.getCustomerId());
        msgIdMappingVo.setMessageId(msgIdMappingDo.getMessageId());
        return msgIdMappingVo;
    }

    @Override
    @Transactional
    @ShardingSphereTransactionType(TransactionType.BASE)
    public void regularReturnPrePayment() {
        LambdaQueryWrapper<MsgRecordDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MsgRecordDo::getFinalResult, RequestEnum.UN_KNOW.getCode())
                .eq(MsgRecordDo::getSendResult, DeliveryEnum.UN_KNOW.getCode())
                .eq(MsgRecordDo::getConsumeCategory, PaymentTypeEnum.SET_MEAL.getCode())
                .lt(MsgRecordDo::getCreateTime, new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L))
                .gt(MsgRecordDo::getCreateTime, new Date(System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000L))
                .orderByDesc(MsgRecordDo::getCreator);

        List<MsgRecordDo> results = msgRecordDao.selectList(queryWrapper);

        Map<String, List<MsgRecordDo>> recordListByCustomerMap = results.stream().collect(Collectors.groupingBy(MsgRecordDo::getCreator));
        //某个customer 的所有消息
        for (Map.Entry<String, List<MsgRecordDo>> recordListByCustomerEntry : recordListByCustomerMap.entrySet()) {
            String customerId = recordListByCustomerEntry.getKey();
            List<MsgRecordDo> recordListByCustomer = recordListByCustomerEntry.getValue();

            Map<String, List<MsgRecordDo>> recordListByAccountMap = recordListByCustomer.stream().collect(Collectors.groupingBy(MsgRecordDo::getAccountId));
            //某个customer 的 AccountId的所有消息
            for (Map.Entry<String, List<MsgRecordDo>> recordListByAccountEntry : recordListByAccountMap.entrySet()) {
                String accountId = recordListByAccountEntry.getKey();
                List<MsgRecordDo> allRecordsByAccount = recordListByAccountEntry.getValue();
                Map<Integer, List<MsgRecordDo>> recordListByAccountTypeMap = allRecordsByAccount.stream().filter(re -> Objects.nonNull(re.getAccountType())).collect(Collectors.groupingBy(MsgRecordDo::getAccountType));

                //退费, 某个账号类型(1-5G消息、2-视频短信消息、3-短信消息)所有消息
                for (Map.Entry<Integer, List<MsgRecordDo>> recordListByAccountTypeEntry : recordListByAccountTypeMap.entrySet()) {
                    Integer accountType = recordListByAccountTypeEntry.getKey();
                    List<MsgRecordDo> recordListByAccountType = recordListByAccountTypeEntry.getValue();
                    //5G消息 ,  就还需要区分消息子类型(session. text, rich)
                    if (accountType == MsgTypeEnum.M5G_MSG.getCode()) {
                        // conversation + text + rich
                        List<MsgRecordDo> conversationMsgRecords = CollectionUtil.newArrayList();
                        List<MsgRecordDo> textMsgRecords = CollectionUtil.newArrayList();
                        List<MsgRecordDo> richMsgRecords = CollectionUtil.newArrayList();
                        for (MsgRecordDo recordByAccountType : recordListByAccountType) {
                            //消息类型  1 文本  2 图片 3 视频 4 音频  6 单卡 7 多卡 8  位置
                            MsgSubTypeEnum subType = recordByAccountType.getConversationId() != null
                                    ? MsgSubTypeEnum.CONVERSATION
                                    : MsgSubTypeEnum.convertTemplateType2MsgSubType(recordByAccountType.getMessageType());
                            if (subType == MsgSubTypeEnum.CONVERSATION) {
                                conversationMsgRecords.add(recordByAccountType);
                            } else if (subType == MsgSubTypeEnum.TEXT) {
                                textMsgRecords.add(recordByAccountType);
                            } else if (subType == MsgSubTypeEnum.RICH) {
                                richMsgRecords.add(recordByAccountType);
                            }
                        }
                        log.info("textMsgRecords:{}", textMsgRecords);
                        log.info("richMsgRecords:{}", richMsgRecords);
                        log.info("conversationMsgRecords:{}", conversationMsgRecords);
                        if (!textMsgRecords.isEmpty()) {
                            log.info("尝试退回5G文本消息套餐余额,客户号:{}, 账号：{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "TEXT", textMsgRecords.size());
                            prepaymentApi.returnRemaining(accountId, MsgTypeEnum.M5G_MSG, MsgSubTypeEnum.TEXT, (long) textMsgRecords.size());
                            log.info("退回5G文本消息套餐余额成功,客户号:{}, 账号：{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "TEXT", textMsgRecords.size());
                        }
                        if (!richMsgRecords.isEmpty()) {
                            log.info("尝试退回5G富文本消息套餐余额,客户号:{},账号:{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "RICH", richMsgRecords.size());
                            prepaymentApi.returnRemaining(accountId, MsgTypeEnum.M5G_MSG, MsgSubTypeEnum.RICH, (long) richMsgRecords.size());
                            log.info("退回5G富文本消息套餐余额成功,客户号:{},账号:{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "RICH", richMsgRecords.size());
                        }
                        if (!conversationMsgRecords.isEmpty()) {
                            log.info("尝试退回5G会话套餐余额,客户号:{},账号:{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "CONVERSATION", conversationMsgRecords.size());
                            prepaymentApi.returnRemaining(accountId, MsgTypeEnum.M5G_MSG, MsgSubTypeEnum.CONVERSATION, (long) conversationMsgRecords.size());
                            log.info("退回5G会话套餐余额成功,客户号:{},账号:{}, 消息类型:{}, 消息子类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.M5G_MSG, "CONVERSATION", conversationMsgRecords.size());
                        }
                    }
                    //非5G消息
                    else {
                        log.info("尝试退回短信/视频短信套餐余额,客户号:{},账号:{}, 消息类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.getValue(accountType), recordListByAccountType.size());
                        prepaymentApi.returnRemaining(accountId, MsgTypeEnum.getValue(accountType), null, (long) recordListByAccountType.size());
                        log.info("退回短信/视频短信套餐余额成功,客户号:{},账号:{}, 消息类型:{}, 数量:{}", customerId, accountId, MsgTypeEnum.getValue(accountType), recordListByAccountType.size());
                    }
                }
            }
            //批量将msgRecord记录设置为发送失败
            if (!recordListByCustomer.isEmpty()) {
                List<Long> ids = recordListByCustomer.stream().map(MsgRecordDo::getId).collect(Collectors.toList());
                LambdaUpdateWrapper<MsgRecordDo> wrapper = new LambdaUpdateWrapper<>();
                wrapper.set(MsgRecordDo::getSendResult, DeliveryEnum.FAILED.getCode())
                        .set(MsgRecordDo::getFinalResult, RequestEnum.FAILED.getCode())
                        .set(MsgRecordDo::getUpdateTime, new Date())
                        .in(MsgRecordDo::getId, ids)
                        .eq(MsgRecordDo::getCreator, customerId);
                msgRecordDao.update(null, wrapper);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateStatusBatchByMsgId(UpdateStatusBatchByMessageIdReq req) {
        return msgRecordDao.updateStatusBatchByMsgId(req.getSendResult(), req.getFinalResult(), req.getMessageIds(), new Date(), "msg_record_" + req.getCustomerId().substring(0, 10));
    }

    @Override
    public void updateMsgRecordConsumeCategory(String localMessageId, String phoneNum, String customerId) {
        msgRecordDao.updateMsgRecordConsumeCategory(localMessageId, phoneNum, "msg_record_" + customerId.substring(0, 10));
    }

    @Override
    public List<String> queryBatchPhoneListByMessageId(String localMessageId) {
        @SuppressWarnings("unchecked")
        LambdaQueryWrapper<MsgRecordDo> qw = new LambdaQueryWrapper<MsgRecordDo>()
                .eq(MsgRecordDo::getMessageId, localMessageId)
                .select(MsgRecordDo::getPhoneNum);
        return msgRecordDao.selectList(qw).stream().map(MsgRecordDo::getPhoneNum).collect(Collectors.toList());
    }
}
