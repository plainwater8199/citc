package com.citc.nce.im.group;

import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.im.broadcast.BroadcastPlanService;
import com.citc.nce.im.service.SendResultService;
import com.citc.nce.robot.api.MessageApi;
import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.SendMsgReq;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.citc.nce.im.broadcast.utils.BroadcastConstants._5G_SENT_CODE_SUCCESS;

@RestController
public class MessageController implements MessageApi {

    @Resource
    SendResultService sendResultService;

    @Autowired
    private BroadcastPlanService broadcastPlanService;

    @Override
    @PostMapping("/im/plan/start")
    public MessageData planStart(@RequestBody StartReq startReq) {
        broadcastPlanService.startPlan(startReq.getPlanId());
        MessageData messageData = new MessageData();
        messageData.setCode(_5G_SENT_CODE_SUCCESS);
        messageData.setMessage("启动成功");
        return messageData;
    }

    @Override
    public Map<String, Map<String, Integer>> tryStartPlan(Long planId) {
        return broadcastPlanService.tryStartPlan(planId);
    }

    @Override
    public GroupSendValidResult tryBalanceStartPlan(Long planId) {
        return broadcastPlanService.tryBalanceStartPlan(planId);
    }

    @PostMapping("/im/plan/count")
    @Override
    public List<MessageCount> planCount(@RequestBody CountReq countReq) {
        return sendResultService.planCount(countReq.getPlanDetailId());
    }

    @Override
    @PostMapping("/im/plan/suspend")
    @ApiOperation("暂停计划")
    public void suspend(@RequestBody StartReq startReq) {
        broadcastPlanService.stopPlan(startReq.getPlanId());
    }

    @Override
    @PostMapping("/im/plan/stop")
    public void closePlan(@RequestBody StartReq startReq) {
        broadcastPlanService.closePlan(startReq.getPlanId());
    }

    @PostMapping(path = "/im/message/checkContacts",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public CheckResult checkContacts(@RequestPart(value = "file") MultipartFile file) {
        return sendResultService.checkContacts(file);
    }

    @PostMapping("/im/plan/testSendMessage")
    @Override
    @XssCleanIgnore
    public MessageData testSendMessage(@RequestBody TestSendMsgReq testSendMsgReq) {
        return sendResultService.testSendMessage(testSendMsgReq);
    }

    @PostMapping("/im/plan/media/testSendMessage")
    @Override
    @XssCleanIgnore
    public RichMediaResultArray mediaTestSendMessage(@RequestBody TestSendMsgReq testSendMsgReq) {
        return sendResultService.mediaTestSendMessage(testSendMsgReq);
    }

    @PostMapping("/im/plan/media/mediaSendMessage")
    @Override
    @XssCleanIgnore
    public RichMediaResultArray mediaSendMessage(@RequestBody SendMsgReq sendMsgReq) {
        return sendResultService.mediaSendMessage(sendMsgReq);
    }
}
