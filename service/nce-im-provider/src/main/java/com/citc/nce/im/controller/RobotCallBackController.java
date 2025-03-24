package com.citc.nce.im.controller;

import com.citc.nce.im.session.processor.ncenode.CallBackService;
import com.citc.nce.im.session.processor.ncenode.CommandService;
import com.citc.nce.robot.api.RobotCallBackApi;
import com.citc.nce.robot.vo.MaterialSubmitReq;
import com.citc.nce.robot.vo.MaterialSubmitResp;
import com.citc.nce.robot.vo.RobotCallBackParam;
import com.citc.nce.robot.vo.RobotCustomCommandReq;
import com.citc.nce.robot.vo.RobotCustomCommandResp;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>机器人自定义指令回调</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2022/12/1 11:13
 */
@RestController
public class RobotCallBackController implements RobotCallBackApi {

    @Resource
    CallBackService callBackService;

    @Resource
    CommandService commandService;

    @Override
    @ApiOperation(value = "自定义指令回调")
    @PostMapping("/im/robot/callBack")
    public void callBackMethod(@RequestBody RobotCallBackParam callBackParam) {
        callBackService.callBackMethod(callBackParam);
    }

    @Override
    @ApiOperation(value = "自定义指令回调")
    @PostMapping("/im/robot/customCommand/getParamForSendMsg")
    public RobotCustomCommandResp getParamForSendMsg(@RequestBody RobotCustomCommandReq req) {
        return commandService.getParamForSendMsg(req);
    }

    @Override
    @ApiOperation(value = "自定义指令送审")
    @PostMapping(value = "/im/robot/customCommand/materialSubmit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public MaterialSubmitResp materialSubmit(MaterialSubmitReq req) {
        return commandService.materialSubmit(req);
    }

    @Override
    @ApiOperation(value = "机器人自定义指令-改变量")
    @PostMapping("/im/robot/customCommand/variableEditByName")
    public RobotCustomCommandResp variableEditByName(@RequestBody RobotCustomCommandReq req) {
        return commandService.variableEditByName(req);
    }

    @Override
    @ApiOperation(value = "机器人自定义指令-查变量")
    @PostMapping("/im/robot/customCommand/variableList")
    public RobotCustomCommandResp variableList(@RequestBody RobotCustomCommandReq req) {
        return commandService.variableList(req);
    }
}
