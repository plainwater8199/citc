package com.citc.nce.robot.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.citc.nce.common.constants.NodeTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.exception.RobotErrorCode;
import com.citc.nce.robot.service.RobotProcessTreeService;
import com.citc.nce.robot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.controller
 * @Author: weilanglang
 * @CreateTime: 2022-07-05  10:25
 * @Description: 流程设计控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/robot")
@Slf4j
public class RobotProcessTreeController implements RobotProcessTreeApi {

    @Resource
    RobotProcessTreeService processTreeService;

    /**
     * 根据流程id，查询设计图
     */
    @XssCleanIgnore
    @PostMapping("/process/tree/list")
    public RobotProcessTreeResp listProcessDesById(RobotProcessTreeReq processTreeReq) {
        long processId = processTreeReq.getProcessId();
        if (ObjectUtil.isEmpty(processId) || processId == 0) {
            throw new BizException(RobotErrorCode.PROCESSDES_BAD_REQUEST);
        }
        return processTreeService.listProcessDesById(processId);
    }

    @PostMapping("/process/tree/statusCallback")
    public void processStatusCallback(@RequestBody @Validated ProcessStatusReq processStatusReq) {
        processTreeService.processStatusCallback(processStatusReq);
    }

    /**
     * 查询流程图(发布状态)
     */
    @Override
    @PostMapping("/release/process/list")
    @XssCleanIgnore
    public RobotProcessTreeResp listReleaseProcessDesById(RobotProcessTreeReq processTreeReq) {
        long processId = processTreeReq.getProcessId();
        if (ObjectUtil.isEmpty(processId) || processId == 0) {
            throw new BizException(RobotErrorCode.PROCESSDES_BAD_REQUEST);
        }
        BaseUser user = SessionContextUtil.getUser();
//        if (ObjectUtil.isNull(user)){
//            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
//        }
        RobotProcessTreeResp robotProcessTreeResp = processTreeService.listReleaseProcessDesById(processId);
        return robotProcessTreeResp;
    }

    /**
     * 预发布，点击发布按钮
     *
     * @param processTreeReq
     * @return
     */
    @PostMapping("process/tree/preRelease")
    @XssCleanIgnore
    public PrepareForReleaseProcessResp prepareForReleaseProcess(@RequestBody RobotProcessTreeReq processTreeReq) {
        //redis保存map数据，key为ProcessTreeList，map key为process+场景ID
        long processId = processTreeReq.getProcessId();
        String processDes = processTreeReq.getProcessDes();

        int status = processDesCheck(processId, processDes);
        if (status == 1) {
            throw new BizException(RobotErrorCode.PROCESSDES_BAD_REQUEST);
        } else if (status == 2) {
            //节点名称重复
            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_NODENAME);
        } else if (status == 3) {
            //子流程不能与当前流程一直
            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_SUBPROCESSID);
        } else if (status == 4) {
            throw new BizException(RobotErrorCode.PROCESSDES_QUESTION_BODY_EMPTY);
        }

        BaseUser user = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(user)) {
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        PrepareForReleaseProcessResp prepareForReleaseProcessResp = processTreeService.prepareForReleaseProcess(processTreeReq, processId, user);
        return prepareForReleaseProcessResp;
    }

    /**
     * 根据流程id，发布该设计图
     */
    @PostMapping("/process/tree/release")
    public RobotProcessTemplateAuditResp releaseProcessDes(RobotProcessTreeReq processTreeReq) {
        //redis保存map数据，key为ProcessTreeList，map key为process+场景ID
        long processId = processTreeReq.getProcessId();
//        String processDes = processTreeReq.getProcessDes();
//
//        int status = processDesCheck(processId, processDes);
//        if (status==1){
//            throw new BizException(RobotErrorCode.PROCESSDES_BAD_REQUEST);
//        }else if (status==2){
//            //节点名称重复
//            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_NODENAME);
//        }else if (status==3){
//            //子流程不能与当前流程一直
//            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_SUBPROCESSID);
//        }

        BaseUser user = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(user)) {
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }

        return processTreeService.releaseProcessDesById(processId, user.getUserId());
    }

    @GetMapping("/process/tree/oldProcessInit")
    public void oldProcessToAuditInit() {
        processTreeService.oldProcessToAuditInit();
    }

    /**
     * 保存流程图
     * 首先保存在redis里，然后数据库也保留一份
     */
    @PostMapping("/process/tree/save")
    @XssCleanIgnore
    public void saveProcessDes(RobotProcessTreeReq processTreeReq) {

        long processId = processTreeReq.getProcessId();
        String processDes = processTreeReq.getProcessDes();
        log.info("原始流程图------ processDes{}", processDes);
        int status = processDesCheck(processId, processDes);
        if (status == 1) {
            throw new BizException(RobotErrorCode.PROCESSDES_BAD_REQUEST);
        } else if (status == 2) {
            //节点名称重复
            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_NODENAME);
        } else if (status == 3) {
            //子流程不能与当前流程一直
            throw new BizException(RobotErrorCode.PROCESSDES_REPEAT_SUBPROCESSID);
        } else if (status == 4) {
            throw new BizException(RobotErrorCode.PROCESSDES_QUESTION_BODY_EMPTY);
        }
        log.info("check后流程图------ processDes{}", processTreeReq.getProcessDes());
        processTreeService.saveProcessDes(processTreeReq);
    }

    @Override
    @PostMapping("/process/ask/list")
    public RobotProcessAskNodeResp listAllAskNode() {
        return processTreeService.listAllAskNode();
    }


    /**
     * 保存或发布时  进行判断
     */

    public int processDesCheck(long processId, String processDes) {
        int errorCode = 0;
        if (StrUtil.isBlank(processDes) || ObjectUtil.isNull(processDes) || ObjectUtil.isNull(processId) || processId == 0) {
            errorCode = 1;
            return errorCode;
        }
        //设计图节点名称不能冲突     子流程id不能与当前流程id一致
        JSONObject jsonObject = JSON.parseObject(processDes);
        JSONArray robotProcessNodeList = jsonObject.getJSONArray("robotProcessNodeList");
        ArrayList<String> nodeNameList = new ArrayList<>();
        for (int i = 0; i < robotProcessNodeList.size(); i++) {
            JSONObject o = robotProcessNodeList.getJSONObject(i);
            String nodeName = o.getString("nodeName");

            if (nodeNameList.contains(nodeName)) {
                errorCode = 2;
                return errorCode;
            }

            if (5 == o.getLong("nodeType")) {
                //如果是5   子流程节点   需要判断子流程不能未当前流程
                JSONArray subProcessList = o.getJSONArray("subProcessList");
                for (int j = 0; j < subProcessList.size(); j++) {
                    Object subProcessNode = subProcessList.get(j);
                    Long subProcessValue = JSON.parseObject(subProcessNode.toString()).getLong("SubProcessValue");
                    if (ObjectUtil.isNotNull(subProcessValue) && subProcessValue != 0 && subProcessValue == processId) {
                        errorCode = 3;
                        return errorCode;
                    }
                }
            }
            //提问节点快捷按钮不为空时，不能没有提问内容
            if (NodeTypeEnum.QUESTION_NODE.getCode() == o.getLong("nodeType")) {
                JSONArray body = o.getJSONArray("contentBody");
                JSONArray buttons = o.getJSONArray("buttonList");
                if (buttons != null && !buttons.isEmpty() && (body == null || body.isEmpty())) {
                    return 4;
                }
            }

            nodeNameList.add(nodeName);
        }
        return errorCode;
    }

    @PostMapping("/process/checkUsed")
    @Override
    public List<String> checkUsed() {
        return processTreeService.checkUsed();
    }

    /**
     * 检查所有未删除流程占用的素材
     */
    @GetMapping("/process/checkUsedByAll")
    @Override
    public boolean checkUsedByAll(@RequestParam("fileUuid") String fileUuid) {
        return processTreeService.checkUsedByAll(fileUuid);
    }

    @PostMapping("/process/getRobotShortcutButtonResp")
    @Override
    public RobotShortcutButtonResp getRobotShortcutButtonResp(@RequestBody String uuid) {
        return processTreeService.getRobotShortcutButtonResp(uuid);
    }
}
