package com.citc.nce.robot.api;

import com.citc.nce.robot.vo.NodeActResult;
import com.citc.nce.robot.vo.UpMsgReq;
import com.citc.nce.robot.vo.UpMsgResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "im-service",contextId="robotReceiveMsg", url = "${im:}")
public interface RobotProcessorApi {
    /**
     * 机器人消息处理
     *
     * @param
     * @return
     */
    @PostMapping("/im/robot/receiveMsg")
    NodeActResult receiveMsg(@RequestBody UpMsgReq upMsgReq) throws Exception;

    /**
     * 发送消息处理
     *
     * @param
     * @return
     */
    @PostMapping("/im/robot/sendMsg")
    UpMsgResp sendMsg(@RequestBody String param) throws Exception;

    /**
     * java发送消息
     *
     * @param
     * @return
     */
    @PostMapping("/im/robot/javaSendMsg")
    void javaSendMsg(@RequestBody UpMsgReq upMsgReq) throws Exception;
}
