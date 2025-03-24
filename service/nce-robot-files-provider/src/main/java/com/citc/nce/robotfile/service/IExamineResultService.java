package com.citc.nce.robotfile.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.dto.PageReq;
import com.citc.nce.robotfile.entity.ExamineResultDo;
import com.citc.nce.vo.AccountResp;
import com.citc.nce.vo.ExamineResultResp;
import com.citc.nce.vo.ExamineResultVo;
import com.citc.nce.vo.FileExamineStatusDto;

import java.util.List;


public interface IExamineResultService extends IService<ExamineResultDo> {
    List<AccountResp> getAccountList(PageReq req, String fileUrl, List<AccountManagementResp> list);

    ExamineResultVo queryExamineResult(ExamineResultResp examineResultResp);

    Long updateMaterialFromChatbotUpdate(String oldChatbotId);

    void deleteAuditRecord(String chatbotAccount);

    List<FileExamineStatusDto> queryExamineResultBatch(List<String> uuids);
}
