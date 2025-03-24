package com.citc.nce.tenant.service;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.robot.vo.SendMessageNumberDetail;
import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import com.citc.nce.tenant.vo.req.MsgIdMappingVo;
import com.citc.nce.tenant.vo.req.MsgRecordVo;
import com.citc.nce.tenant.vo.req.MsgSendDetailReq;
import com.citc.nce.tenant.vo.req.MsgWithdrawIdReq;
import com.citc.nce.tenant.vo.req.UpdateByPhoneAndMessageIdReq;
import com.citc.nce.tenant.vo.req.UpdateStatusBatchByMessageIdReq;
import com.citc.nce.tenant.vo.resp.MsgRecordResp;
import com.citc.nce.tenant.vo.resp.MsgSendDetailResultResp;
import com.citc.nce.tenant.vo.resp.NodeResp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MsgRecordService {
    void insert(MsgRecordVo req);

    List<String> queryPhoneList(Long planDetailId, int accountType, List<Integer> msgStatusList, String date);

    List<String> queryUnReadFor5G(Long nodeId, String date);

    List<String> queryNotClick(String date, Long parentNodeId);

    void insertBatch(List<MsgRecordVo> msgRecordReqs);

    Map<Integer, Long> countByMsgStatus(Long nodeId);


    Integer updateByPhoneAndMessageId(UpdateByPhoneAndMessageIdReq req);

    MsgRecordVo selectByPhoneAndMessageId(Integer msgType, String phoneNum, String messageId, String customerId);

    void updateMessageId(Integer msgType, String oldMessageId, String messageId, String customerId);

    long selectCountByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String messageId);

    void updateReceiptTimeByByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String messageId, Date receiptTime);

    PageResult<MsgSendDetailResultResp> selectSendDetail(MsgSendDetailReq queryReq);

    Boolean withdraw(MsgWithdrawIdReq msgWithdrawIdReq);

    Long queryNodeIdByMessageId(String messageId, String customerId);

    NodeResp queryNodeByMessageId(String messageId, String customerId);


    /**
     * 查询客户消息发送数量详情
     *
     * @param customerId 客户id
     * @param start      起始时间
     * @param end        结束时间
     * @return
     */
    SendMessageNumberDetail querySendNumberDetail(String customerId, LocalDateTime start, LocalDateTime end);

    List<SimpleMessageSendNumberDetail> queryFallbackMessageSendNumberDetail(String customerId, LocalDateTime start, LocalDateTime end);

    String queryAccountIdByMessageId(String messageId);

    MsgRecordResp queryRecordByMessageId(String messageId);

    MsgRecordResp queryRecordByMessageIdAndPhoneNumber(String messageId, String phoneNumber, String customerId);

    List<MsgRecordResp> queryRecordByMessageIdAndCustomerId(String messageId, String customerId);

    Boolean existsMsgRecordByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId);

    Boolean existsMsgRecordByMsgTypeAndBetweenDate(String customerId, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end);

    Boolean existsMsgRecordByMsgTypeAndAccountIdAndBetweenDate(String accountId, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end);

    Map<MsgTypeEnum, List<MessageResourceType>> querySendMsgTypeListBetween(String customerId, LocalDateTime start, LocalDateTime end);

    List<MsgRecordVo> selectSendDetailByPlanDetailIds(List<Long> planDetailIds);

    void insertMsgIdMapping(MsgIdMappingVo req);

    String queryMsgIdMapping(String platformMsgId);

    MsgIdMappingVo queryMsgMapping(String platformMsgId);

    void regularReturnPrePayment();

    Integer updateStatusBatchByMsgId(UpdateStatusBatchByMessageIdReq req);

    void updateMsgRecordConsumeCategory(String localMessageId, String phoneNum, String customerId);

    List<String> queryBatchPhoneListByMessageId(String localMessageId);
}
