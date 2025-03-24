package com.citc.nce.rebot.manage;

import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.rebot.manage
 * @Author: weilanglang
 * @CreateTime: 2022-07-12  16:20
 * @Description: 机器人设置--流程设计facade层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/robot")
@Api(value = "processDes",tags = "机器人设置--流程图设计API")
public class RobotProcessDesController {

    @Resource
    RobotProcessTreeApi robotProcessTreeApi;


    /**
     * 根据流程ID，查询设计图
     * */
    @XssCleanIgnore
    @PostMapping("/process/tree/list")
    @ApiOperation(value = "流程图查询", notes = "流程图查询")
    public RobotProcessTreeResp listProcessDesById(@RequestBody RobotProcessTreeReq processTreeReq){
        return robotProcessTreeApi.listProcessDesById(processTreeReq);
    }
    /**
     * 根据流程ID，查询设计图(发布状态)
     * */
    @PostMapping("/release/process/list")
    @ApiOperation(value = "流程图查询(发布状态)", notes = "流程图查询(发布状态)")
    @XssCleanIgnore
    public RobotProcessTreeResp listReleaseProcessDesById(@RequestBody RobotProcessTreeReq processTreeReq){
        return robotProcessTreeApi.listReleaseProcessDesById(processTreeReq);
    }

    /**
     *  根据流程ID，发布该设计图
     * */
    @PostMapping("/process/tree/release")
    @ApiOperation(value = "流程图发布", notes = "流程图发布")
    public RobotProcessTemplateAuditResp releaseProcessDes(@RequestBody RobotProcessTreeReq processTreeReq){
       return robotProcessTreeApi.releaseProcessDes(processTreeReq);
    }
    @PostMapping("/process/tree/preRelease")
    @ApiOperation(value = "流程图预发布", notes = "流程图预发布")
    @XssCleanIgnore
    public PrepareForReleaseProcessResp prepareForReleaseProcess(@RequestBody RobotProcessTreeReq processTreeReq)
    {
        return robotProcessTreeApi.prepareForReleaseProcess(processTreeReq);
    }

    /**
     * 保存流程设计
     * */
    @XssCleanIgnore
    @PostMapping("/process/tree/save")
    @ApiOperation(value = "流程图保存", notes = "流程图保存")
    public void saveProcessDes(@RequestBody RobotProcessTreeReq processTreeReq){
        robotProcessTreeApi.saveProcessDes(processTreeReq);
    }

    /**
     * 查询所有提问节点信息
     * */
    @GetMapping("/process/ask/list")
    @ApiOperation(value = "提问节点查询", notes = "提问节点查询")
    public RobotProcessAskNodeResp listAskNode(){
        return robotProcessTreeApi.listAllAskNode();
    }


}
