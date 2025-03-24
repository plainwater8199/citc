package com.citc.nce.tenant;

import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.vo.SendMessageNumberDetail;
import com.citc.nce.robot.vo.messagesend.SimpleMessageSendNumberDetail;
import com.citc.nce.tenant.service.MsgRecordService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController()
@Slf4j
public class MsgRecordController implements MsgRecordApi {

    @Resource
    private MsgRecordService msgRecordService;

    @Override
    @ApiOperation("插入数据")
    @PostMapping("/msg/insert")
    @XssCleanIgnore
    public void insert(MsgRecordVo req) {
        msgRecordService.insert(req);
    }

    @Override
    public void insertMsgIdMapping(MsgIdMappingVo req) {
        msgRecordService.insertMsgIdMapping(req);
    }

    @Override
    public String queryMsgIdMapping(String platformMsgId) {
        return msgRecordService.queryMsgIdMapping(platformMsgId);
    }

    @Override
    @XssCleanIgnore
    public MsgIdMappingVo queryMsgMapping(String platformMsgId) {
        return msgRecordService.queryMsgMapping(platformMsgId);
    }

    @Override
    @ApiOperation("批量插入数据")
    @PostMapping("/msg/insertBatch")
    @XssCleanIgnore
    public void insertBatch(List<MsgRecordVo> msgRecordReqs) {
        msgRecordService.insertBatch(msgRecordReqs);
    }

    @Override
    @ApiOperation("批量修改数据状态")
    public Integer updateStatusBatchByMsgId(UpdateStatusBatchByMessageIdReq req) {
        return msgRecordService.updateStatusBatchByMsgId(req);
    }

    @Override
    @ApiOperation("根据发送结果类型查询所有手机号")
    @PostMapping("/msg/queryPhoneList")
    public List<String> queryPhoneList(Long planDetailId, int accountType, List<Integer> msgStatusList, String date) {
        return msgRecordService.queryPhoneList(planDetailId, accountType, msgStatusList, date);
    }

    @Override
    @ApiOperation("查询未读的手机号码列表")
    @PostMapping("/msg/queryUnReadFor5G")
    public List<String> queryUnReadFor5G(Long nodeId, String date) {
        return msgRecordService.queryUnReadFor5G(nodeId, date);
    }

    @Override
    @ApiOperation("查询未点击消息按钮的手机列表")
    @PostMapping("/msg/queryNotClick")
    public List<String> queryNotClick(String date, Long parentNodeId) {
        return msgRecordService.queryNotClick(date, parentNodeId);
    }

    @Override
    @ApiOperation("根据消息状态查询消息数目")
    @PostMapping("/msg/countByMsgStatus")
    public Map<Integer, Long> countByMsgStatus(Long nodeId) {
        return msgRecordService.countByMsgStatus(nodeId);
    }


    @Override
    @ApiOperation("根据手机号和消息id更新数据")
    @PostMapping("/msg/updateByPhoneAndMessageId")
    public Integer updateByPhoneAndMessageId(UpdateByPhoneAndMessageIdReq req) {
        return msgRecordService.updateByPhoneAndMessageId(req);
    }

    @Override
    @ApiOperation("根据手机号和消息id查询数据")
    @PostMapping("/msg/selectByPhoneAndMessageId")
    @XssCleanIgnore
    public MsgRecordVo selectByPhoneAndMessageId(Integer msgType, String phoneNum, String messageId, String customerId) {
        return msgRecordService.selectByPhoneAndMessageId(msgType, phoneNum, messageId, customerId);
    }

    @Override
    @ApiOperation("根据手机号和消息id查询数据")
    @PostMapping("/msg/updateMessageId")
    public void updateMessageId(Integer msgType, String oldMessageId, String messageId, String customerId) {
        msgRecordService.updateMessageId(msgType, oldMessageId, messageId, customerId);
    }

    @Override
    @ApiOperation("根据手机号和消息id查询数据")
    @PostMapping("/msg/selectCountByPlanDetailIdAndMessageId")
    public long selectCountByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String messageId) {
        return msgRecordService.selectCountByPlanDetailIdAndMessageId(msgType, planDetailId, messageId);
    }

    @Override
    @ApiOperation("根据手机号和消息id查询数据")
    @PostMapping("/msg/updateReceiptTimeByByPlanDetailIdAndMessageId")
    public void updateReceiptTimeByByPlanDetailIdAndMessageId(Integer msgType, Long planDetailId, String messageId, Date receiptTime) {
        msgRecordService.updateReceiptTimeByByPlanDetailIdAndMessageId(msgType, planDetailId, messageId, receiptTime);
    }

    @Override
    @ApiOperation("查询发送详情")
    @PostMapping("/msg/selectSendDetail")
    @XssCleanIgnore
    public PageResult<MsgSendDetailResultResp> selectSendDetail(MsgSendDetailReq queryReq) {
        return msgRecordService.selectSendDetail(queryReq);
    }

    @Override
    @ApiOperation("撤回消息")
    @PostMapping("/msg/withdraw")
    public Boolean withdraw(MsgWithdrawIdReq msgWithdrawIdReq) {
        return msgRecordService.withdraw(msgWithdrawIdReq);
    }

    @Override
    @XssCleanIgnore
    public MsgRecordResp queryRecordByMessageId(String messageId) {
        return msgRecordService.queryRecordByMessageId(messageId);
    }

    @Override
    @XssCleanIgnore
    public MsgRecordResp queryRecordByMessageIdAndPhoneNumber(String messageId, String phoneNumber, String customerId) {
        return msgRecordService.queryRecordByMessageIdAndPhoneNumber(messageId, phoneNumber, customerId);
    }

    @Override
    @XssCleanIgnore
    public List<MsgRecordResp> queryRecordByMessageIdAndCustomerId(String messageId, String customerId) {
        return msgRecordService.queryRecordByMessageIdAndCustomerId(messageId, customerId);
    }

//    @ApiOperation("根据message_id查询nodeId")
//    @Override
//    @GetMapping("/msg/queryNodeIdByMessageId")
//    public Long queryNodeIdByMessageId(@RequestParam("messageId") String messageId) {
//        return msgRecordService.queryNodeIdByMessageId(messageId);
//    }

    @Override
    @ApiOperation("根据message_id查询nodeId")
    @GetMapping("/msg/queryNodeIdByMessageId")
    public Long queryNodeIdByMessageId(@RequestParam("messageId") String messageId, @RequestParam("customerId") String customerId) {
        return msgRecordService.queryNodeIdByMessageId(messageId, customerId);
    }

    @Override
    public NodeResp queryNodeByMessageId(String messageId, String customerId) {
        return msgRecordService.queryNodeByMessageId(messageId, customerId);
    }

    @Override
    public String queryAccountIdByMessageId(String messageId) {
        return msgRecordService.queryAccountIdByMessageId(messageId);
    }

    @Override
    public SendMessageNumberDetail querySendNumberDetail(String customerId, LocalDateTime start, LocalDateTime end) {
        return msgRecordService.querySendNumberDetail(customerId, start, end);
    }

    @Override
    public List<SimpleMessageSendNumberDetail> queryFallbackMessageSendNumberDetail(String customerId, LocalDateTime start, LocalDateTime end) {
        return msgRecordService.queryFallbackMessageSendNumberDetail(customerId, start, end);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndAccountId(String customerId, MsgTypeEnum msgType, String accountId) {
        return msgRecordService.existsMsgRecordByMsgTypeAndAccountId(customerId, msgType, accountId);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndBetweenDate(String customerId, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end) {
        return msgRecordService.existsMsgRecordByMsgTypeAndBetweenDate(customerId, msgType, start, end);
    }

    @Override
    public Boolean existsMsgRecordByMsgTypeAndAccountIdAndBetweenDate(String accountId, MsgTypeEnum msgType, LocalDateTime start, LocalDateTime end) {
        return msgRecordService.existsMsgRecordByMsgTypeAndAccountIdAndBetweenDate(accountId, msgType, start, end);
    }

    @Override
    public Map<MsgTypeEnum, List<MessageResourceType>> querySendMsgTypeListBetween(String customerId, LocalDateTime start, LocalDateTime end) {
        return msgRecordService.querySendMsgTypeListBetween(customerId, start, end);
    }

    @Override
    @XssCleanIgnore
    public List<MsgRecordVo> selectSendDetailByPlanDetailIds(List<Long> planDetailIds) {
        return msgRecordService.selectSendDetailByPlanDetailIds(planDetailIds);
    }

    @Override
    public void updateMsgRecordConsumeCategory(String localMessageId, String phoneNum, String customerId) {
        msgRecordService.updateMsgRecordConsumeCategory(localMessageId, phoneNum, customerId);
    }

    @Override
    public List<String> queryBatchPhoneListByMessageId(String localMessageId) {
        return msgRecordService.queryBatchPhoneListByMessageId(localMessageId);
    }
}
