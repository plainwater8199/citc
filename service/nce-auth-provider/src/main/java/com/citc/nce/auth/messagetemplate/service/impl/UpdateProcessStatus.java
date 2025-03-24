package com.citc.nce.auth.messagetemplate.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.auth.messagetemplate.dao.MessageTemplateAuditDao;
import com.citc.nce.auth.messagetemplate.entity.MessageTemplateAuditDo;
import com.citc.nce.common.constants.Constants;
import com.citc.nce.robot.RobotProcessTreeApi;
import com.citc.nce.robot.vo.ProcessStatusReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yy
 * @date 2024-04-02 23:10:44
 */
@Component
@Slf4j
public class UpdateProcessStatus {
    @Resource
    RobotProcessTreeApi processTreeApi;

    @Async
    void updateProcessStatus(Long processId, Long processDesId, int status,Long templateId) {
        log.info("机器人流程模板，审核后回调流程状态更新程序模板状态：{}，流程id：{},模板id：{}", status, processId,templateId);
        //不同步审核中
        if (status == Constants.TEMPLATE_STATUS_PENDING)
            return;
        ProcessStatusReq req = new ProcessStatusReq();
        req.setProcessId(processId);
        req.setStatus(status);
        processTreeApi.processStatusCallback(req);
    }
}
