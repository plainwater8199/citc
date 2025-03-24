package com.citc.nce.robot;

import com.citc.nce.robot.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot
 * @Author: weilanglang
 * @CreateTime: 2022-07-05  10:23
 * @Description: 流程图设计
 * @Version: 1.0
 */
@FeignClient(value = "rebot-service",contextId="processdes-service", url = "${robot:}")
public interface RobotProcessTreeApi {

    /**
     * 根据流程ID，查询设计图
     * */
    @PostMapping("/robot/process/tree/list")
    public RobotProcessTreeResp listProcessDesById(@RequestBody RobotProcessTreeReq processTreeReq);


    /**
     * 根据流程ID，查询设计图（发布状态）
     * */
    @PostMapping("/robot/release/process/list")
    public RobotProcessTreeResp listReleaseProcessDesById(@RequestBody RobotProcessTreeReq processTreeReq);

    @PostMapping("/robot/process/tree/statusCallback")
    public void processStatusCallback(@RequestBody @Validated ProcessStatusReq processStatusReq);
        /**
         *  根据流程ID，发布该设计图
         * */
    @PostMapping("/robot/process/tree/release")
    public RobotProcessTemplateAuditResp releaseProcessDes(@RequestBody RobotProcessTreeReq processTreeReq);
    /**
     *  蜂动上线，旧流程初始化，临时接口
     * */
    @GetMapping("/process/tree/oldProcessInit")
    public void oldProcessToAuditInit();

    @PostMapping("/robot/process/tree/preRelease")
    public PrepareForReleaseProcessResp prepareForReleaseProcess(@RequestBody RobotProcessTreeReq processTreeReq);

    /**
     * 保存流程设计
     * */
    @PostMapping("/robot/process/tree/save")
    public void saveProcessDes(@RequestBody RobotProcessTreeReq processTreeReq);

    /**
     *  查询所有提问节点信息
     * */
    @PostMapping("/robot/process/ask/list")
    public RobotProcessAskNodeResp listAllAskNode();

    /**
     *  检查流程发布后占用的素材
     * */
    @PostMapping("/robot/process/checkUsed")
    public List<String> checkUsed();
    /**
     *  检查所有未删除流程占用的素材
     * */
    @GetMapping("/robot/process/checkUsedByAll")
    public boolean checkUsedByAll(@RequestParam("fileUuid") String fileUuid);
    /**
     *  获取快捷按钮信息
     * */
    @PostMapping("/robot/process/getRobotShortcutButtonResp")
    public RobotShortcutButtonResp getRobotShortcutButtonResp(@RequestBody String uuid);
}
