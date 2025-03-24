package com.citc.nce.tenant;

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
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;


@FeignClient(value = "rebot-service", contextId = "MsgRecordApi", url = "${robot:}")
public interface MsgRecordApi {

    @ApiOperation("插入数据")
    @PostMapping("/msg/insert")
    void insert(@RequestBody MsgRecordVo req);

    @ApiOperation("插入msgId中间表")
    @PostMapping("/msg/insertMsgIdMapping")
    void insertMsgIdMapping(@RequestBody MsgIdMappingVo req);

    @ApiOperation("插入msgId中间表")
    @PostMapping("/msg/queryMsgIdMapping")
    String queryMsgIdMapping(@RequestParam("platformMsgId") String platformMsgId);

    @ApiOperation("插入msgId中间表")
    @PostMapping("/msg/queryMsgMapping")
    MsgIdMappingVo queryMsgMapping(@RequestParam("platformMsgId") String platformMsgId);

    @ApiOperation("批量插入数据")
    @PostMapping("/msg/insertBatch")
    void insertBatch(@RequestBody List<MsgRecordVo> msgRecordReqs);

    @ApiOperation("根据手机号和消息id更新数据")
    @PostMapping("/msg/updateStatusBatchById")
    Integer updateStatusBatchByMsgId(@RequestBody UpdateStatusBatchByMessageIdReq req);

    @ApiOperation("根据发送结果类型查询所有手机号")
    @PostMapping("/msg/queryPhoneList")
    List<String> queryPhoneList(@RequestParam("planId") Long planId, @RequestParam("accountType") int accountType, @RequestParam("msgStatusList") List<Integer> msgStatusList, @RequestParam("date") String date);

    @ApiOperation("查询未读的手机号码列表")
    @PostMapping("/msg/queryUnReadFor5G")
    List<String> queryUnReadFor5G(@RequestParam("nodeId") Long nodeId, @RequestParam("date") String date);

    @ApiOperation("查询未点击消息按钮的手机列表")
    @PostMapping("/msg/queryNotClick")
    List<String> queryNotClick(@RequestParam("date") String date, @RequestParam("parentNodeId") Long parentNodeId);

    @ApiOperation("根据消息状态查询消息数目")
    @PostMapping("/msg/countByMsgStatus")
    Map<Integer, Long> countByMsgStatus(@RequestParam("nodeId") Long nodeId);

    @ApiOperation("根据手机号和消息id更新数据")
    @PostMapping("/msg/updateByPhoneAndMessageId")
    Integer updateByPhoneAndMessageId(@RequestBody UpdateByPhoneAndMessageIdReq req);

    @ApiOperation("根据手机号和消息id查询数据")
    @PostMapping("/msg/selectByPhoneAndMessageId")
    MsgRecordVo selectByPhoneAndMessageId(@RequestParam("msgType") Integer msgType, @RequestParam("phoneNum") String phoneNum, @RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId);

    @ApiOperation("更新messageId")
    @PostMapping("/msg/updateMessageId")
    void updateMessageId(@RequestParam("msgType") Integer msgType, @RequestParam("oldMessageId") String oldMessageId, @RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId);

    @ApiOperation("根据PlanDetailId和MessageId查询数量")
    @PostMapping("/msg/selectCountByPlanDetailIdAndMessageId")
    long selectCountByPlanDetailIdAndMessageId(@RequestParam("msgType") Integer msgType, @RequestParam("planDetailId") Long planDetailId, @RequestParam("messageId") String messageId);

    @ApiOperation("根据PlanDetailId和MessageId更新接受时间")
    @PostMapping("/msg/updateReceiptTimeByByPlanDetailIdAndMessageId")
    void updateReceiptTimeByByPlanDetailIdAndMessageId(@RequestParam("msgType") Integer msgType, @RequestParam("planDetailId") Long planDetailId, @RequestParam("messageId") String messageId, @RequestParam("receiptTime") Date receiptTime);

    @ApiOperation("查询发送详情")
    @PostMapping("/msg/selectSendDetail")
    PageResult<MsgSendDetailResultResp> selectSendDetail(@RequestBody MsgSendDetailReq queryReq);

    @ApiOperation("撤回消息")
    @PostMapping("/msg/withdraw")
    Boolean withdraw(@RequestBody MsgWithdrawIdReq msgWithdrawIdReq);

    @ApiOperation("根据message_id查询nodeId")
    @GetMapping("/msg/queryNodeIdByMessageId")
    Long queryNodeIdByMessageId(@RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId);

    @ApiOperation("根据message_id查询nodeId和operatorCode")
    @GetMapping("/msg/queryNodeByMessageId")
    NodeResp queryNodeByMessageId(@RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId);

    @ApiOperation("根据message_id查询批次的发送参数")
    @GetMapping("/msg/queryRecordByMessageId")
    MsgRecordResp queryRecordByMessageId(@RequestParam("messageId") String messageId);

    @ApiOperation("根据messageId和phoneNumber查询发送记录")
    @GetMapping("/msg/queryRecordByMessageIdAndPhoneNumber")
    MsgRecordResp queryRecordByMessageIdAndPhoneNumber(@RequestParam("messageId") String messageId, @RequestParam("phoneNumber") String phoneNumber, @RequestParam("customerId") String customerId);

    @ApiOperation("根据messageId和phoneNumber查询发送记录")
    @GetMapping("/msg/queryRecordByMessageIdAndCustomerId")
    List<MsgRecordResp> queryRecordByMessageIdAndCustomerId(@RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId);


    @ApiOperation("根据message_id查询发送账号")
    @GetMapping("/msg/queryAccountIdByMessageId")
    String queryAccountIdByMessageId(@RequestParam("messageId") String messageId);

    @PostMapping("/msg/querySendNumberDetail")
    SendMessageNumberDetail querySendNumberDetail(@RequestParam("customerId") String customerId, @RequestParam("start") LocalDateTime start, @RequestParam("end") LocalDateTime end);

    @PostMapping("/msg/queryFallbackMessageSendNumberDetail")
    List<SimpleMessageSendNumberDetail> queryFallbackMessageSendNumberDetail(@RequestParam("customerId") String customerId, @RequestParam("start") LocalDateTime start, @RequestParam("end") LocalDateTime end);

    @GetMapping("/msg/record/exists")
    Boolean existsMsgRecordByMsgTypeAndAccountId(@RequestParam("customerId") String customerId,
                                                 @RequestParam("msgType") MsgTypeEnum msgType,
                                                 @RequestParam("accountId") String accountId);

    @GetMapping("/msg/record/exists/between")
    @ApiOperation("查询日期范围内，客户该消息类型是否有消息记录")
    Boolean existsMsgRecordByMsgTypeAndBetweenDate(@RequestParam("customerId") String customerId,
                                                   @RequestParam("msgType") MsgTypeEnum msgType,
                                                   @RequestParam("start") LocalDateTime start,
                                                   @RequestParam("end") LocalDateTime end);

    @GetMapping("/msg/record/account/exists/between")
    @ApiOperation("查询日期范围内，该消息类型是否有消息记录")
    Boolean existsMsgRecordByMsgTypeAndAccountIdAndBetweenDate(@RequestParam("accountId") String accountId,
                                                               @RequestParam("msgType") MsgTypeEnum msgType,
                                                               @RequestParam("start") LocalDateTime start,
                                                               @RequestParam("end") LocalDateTime end);

    @GetMapping("/msg/record/msgTypes/between")
    @ApiOperation("查询日期范围内，客户发送了哪些消息类型的消息和其对应的消息来源")
    Map<MsgTypeEnum, List<MessageResourceType>> querySendMsgTypeListBetween(@RequestParam("customerId") String customerId,
                                                                            @RequestParam("start") LocalDateTime start,
                                                                            @RequestParam("end") LocalDateTime end);

    @GetMapping("/msg/record/selectSendDetailByPlanDetailIds")
    @ApiOperation("根据计划详情ID获取计划的发送详情")
    List<MsgRecordVo> selectSendDetailByPlanDetailIds(@RequestParam("planDetailIds") List<Long> planDetailIds);

    @ApiOperation("更新message的消费类型")
    @PostMapping("/msg/record/updateMsgRecordConsumeCategory")
    void updateMsgRecordConsumeCategory(@RequestParam("localMessageId") String localMessageId, @RequestParam("phoneNum") String phoneNum, @RequestParam("customerId") String customerId);


    @ApiOperation("根据message id查询此批次的全部手机号")
    @GetMapping("/msg/record/queryBatchPhoneListByMessageId")
    List<String> queryBatchPhoneListByMessageId(@RequestParam("localMessageId") String localMessageId);
}
