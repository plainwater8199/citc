package com.citc.nce.rebot.manage;

import com.citc.nce.robot.RobotVariableApi;
import com.citc.nce.robot.vo.RobotValueResetResp;
import com.citc.nce.robot.vo.RobotVariableCreateReq;
import com.citc.nce.robot.vo.RobotVariablePageReq;
import com.citc.nce.robot.vo.RobotVariableReq;
import com.citc.nce.robot.vo.RobotVariableResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.rebot.manage
 * @Author: weilanglang
 * @CreateTime: 2022-07-12  10:26
 * @Description: 机器人设置--变量管理facade层
 * @Version: 1.0
 */

@RestController
@RequestMapping("/robot")
@Api(value = "variable",tags = "机器人设置--变量管理API")
public class RobotVariableController {

    @Resource
    RobotVariableApi robotVariableApi;


    @PostMapping("/variable/save")
    @ApiOperation(value = "变量保存", notes = "变量保存")
    public void addRotbotVariable(@RequestBody RobotVariableReq robotVariableReq){
        robotVariableApi.save(robotVariableReq);
    }

    @PostMapping("/variable/list")
    @ApiOperation(value = "查询变量列表", notes = "查询变量列表")
    public RobotVariableResp listAllByPage(@RequestBody RobotVariablePageReq robotVariablePageReq){
        return robotVariableApi.listAll(robotVariablePageReq);
    }
    @PostMapping("/variable/edit")
    @ApiOperation(value = "变量编辑", notes = "变量编辑")
    public void compileRobotVariable(@RequestBody RobotVariableReq robotVariableReq){
        robotVariableApi.compile(robotVariableReq);
    }

    @PostMapping("/variable/editByName")
    @ApiOperation(value = "变量编辑", notes = "变量编辑")
    public RobotValueResetResp editByName(@RequestBody RobotVariableReq robotVariableReq){
       return robotVariableApi.editByName(robotVariableReq);
    }

    @PostMapping("/variable/delete")
    @ApiOperation(value = "变量删除", notes = "变量删除")
    public void removeRotbotVariable(@RequestBody RobotVariableReq robotVariableReq){
        robotVariableApi.removeVariable(robotVariableReq);
    }

    @PostMapping("/variable/getList")
    @ApiOperation(value = "自定义指令查询变量列表", notes = "自定义指令查询变量列表")
    public RobotVariableResp getList(@RequestBody RobotVariableCreateReq robotVariableCreateReq){
        return robotVariableApi.getList(robotVariableCreateReq);
    }
}
