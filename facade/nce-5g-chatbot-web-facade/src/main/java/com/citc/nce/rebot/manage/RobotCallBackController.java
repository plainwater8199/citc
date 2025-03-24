package com.citc.nce.rebot.manage;

import com.citc.nce.common.facadeserver.annotations.SkipToken;
import com.citc.nce.robot.api.RobotCallBackApi;
import com.citc.nce.robot.api.RobotProcessorApi;
import com.citc.nce.robot.vo.MaterialSubmitReq;
import com.citc.nce.robot.vo.MaterialSubmitResp;
import com.citc.nce.robot.vo.RobotCallBackParam;
import com.citc.nce.robot.vo.RobotCustomCommandReq;
import com.citc.nce.robot.vo.RobotCustomCommandResp;
import com.citc.nce.robot.vo.UpMsgReq;
import com.citc.nce.robot.vo.UpMsgResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/7 9:52
 */
@Api(value = "robot",tags = "机器人--自定义指令回调")
@RestController
public class RobotCallBackController {
    @Resource
    RobotCallBackApi robotCallBackApi;
    @Resource
    RobotProcessorApi robotProcessorApi;

    @ApiOperation(value = "自定义指令回调")
    @PostMapping("/im/robot/callBack")
    void callBackMethod(@RequestBody RobotCallBackParam callBackParam) {
        robotCallBackApi.callBackMethod(callBackParam);
    }

    @SkipToken
    @ApiOperation(value = "自定义指令回调")
    @PostMapping("/im/robot/customCommand/getParamForSendMsg")
    RobotCustomCommandResp getParamForSendMsg(@RequestBody RobotCustomCommandReq req) {
        return robotCallBackApi.getParamForSendMsg(req);
    }
    
    /**
     * 发送消息处理
     *
     * @param param
     */
    @PostMapping("/im/robot/sendMsg")
    public UpMsgResp sendMsg(@RequestBody String param) throws Exception {
        return robotProcessorApi.sendMsg(param);
    }

    /**
     * 自定义指令送审
     *
     * @param req
     */
    @ApiOperation(value = "自定义指令送审")
    @PostMapping(value = "/im/robot/customCommand/materialSubmit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public MaterialSubmitResp materialSubmit(@RequestBody MaterialSubmitReq req) {
        return robotCallBackApi.materialSubmit(req);
    }

}
