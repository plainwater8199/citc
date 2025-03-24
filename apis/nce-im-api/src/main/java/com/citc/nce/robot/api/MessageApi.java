package com.citc.nce.robot.api;

import com.citc.nce.robot.req.RichMediaResultArray;
import com.citc.nce.robot.req.SendMsgReq;
import com.citc.nce.robot.req.TestSendMsgReq;
import com.citc.nce.robot.vo.CheckResult;
import com.citc.nce.robot.vo.CountReq;
import com.citc.nce.robot.vo.GroupSendValidResult;
import com.citc.nce.robot.vo.MessageCount;
import com.citc.nce.robot.vo.MessageData;
import com.citc.nce.robot.vo.StartReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(value = "im-service", contextId = "MessageApi", url = "${im:}")
public interface MessageApi {

    /**
     * 启动计划
     *
     * @param
     * @return
     */
    @PostMapping("/im/plan/start")
    MessageData planStart(@RequestBody StartReq startReq);

    @GetMapping("/im/plan/tryStart")
    Map<String, Map<String, Integer>> tryStartPlan(@RequestParam("planId") Long planId);

    @GetMapping("/im/plan/tryBalanceStart")
    GroupSendValidResult tryBalanceStartPlan(@RequestParam("planId") Long planId);

    /**
     * 统计数量
     *
     * @param
     * @return
     */
    @PostMapping("/im/plan/count")
    List<MessageCount> planCount(@RequestBody CountReq countReq);

    /**
     * 暂停计划
     *
     * @param
     * @return
     */
    @PostMapping("/im/plan/suspend")
    void suspend(@RequestBody StartReq countReq);

    @PostMapping("/im/plan/stop")
    void closePlan(@RequestBody StartReq startReq);


    /**
     * 检测号码接口
     *
     * @param file 文件
     * @return 检测结果
     */
    @PostMapping(path = "/im/message/checkContacts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CheckResult checkContacts(@RequestPart(value = "file") MultipartFile file);


    /**
     * 测试发送计划接口
     **/
    @PostMapping("/im/plan/testSendMessage")
    MessageData testSendMessage(@RequestBody TestSendMsgReq testSendMsgReq);

    @PostMapping("/im/plan/media/testSendMessage")
    RichMediaResultArray mediaTestSendMessage(@RequestBody TestSendMsgReq testSendMsgReq);

    @PostMapping("/im/plan/media/mediaSendMessage")
    RichMediaResultArray mediaSendMessage(@RequestBody SendMsgReq sendMsgReq);
}
