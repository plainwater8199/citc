package com.citc.nce.robotfile.controller;


import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.fileApi.ExamineResultApi;
import com.citc.nce.robotfile.service.IExamineResultService;
import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import com.citc.nce.vo.FileExamineStatusDto;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @Author: ping chen
 */
@Api(value = "资源审核")
@RestController
public class ExamineResultController implements ExamineResultApi {

    @Resource
    private IExamineResultService iExamineResultService;


    @Override
    public ExamineResultVo queryExamineResult(ExamineResultResp examineResultResp) {
        return iExamineResultService.queryExamineResult(examineResultResp);
    }

    @Override
    public Long updateMaterialFromChatbotUpdate(String oldChatbotId) {
        return iExamineResultService.updateMaterialFromChatbotUpdate(oldChatbotId);
    }

    @Override
    public List<FileExamineStatusDto> queryExamineResultBatch(List<String> uuids) {
        return iExamineResultService.queryExamineResultBatch(uuids);
    }
}
