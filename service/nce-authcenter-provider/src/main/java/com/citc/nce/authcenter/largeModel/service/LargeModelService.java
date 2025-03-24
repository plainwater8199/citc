package com.citc.nce.authcenter.largeModel.service;

import com.citc.nce.authcenter.largeModel.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

public interface LargeModelService {
    PageResult<LargeModelResp> getLargeModelList(LargeModelReq req);

    LargeModelResp getLargeModeDetail(Long id);

    void createLargeModel(LargeModelCreateReq req);

    void updateLargeModel(LargeModelUpdateReq req);

    void deleteLargeModel(LargeModelDetailReq req);

    PageResult<PromptResp> getPromptList(PromptReq req);

    PromptDetailResp getPromptDetail(Long id);

    void updatePrompt(PromptUpdateReq req);

    void updatePromptStatus(PromptUpdateStatusReq req);

    LargeModelPromptSettingResp getLargeModelPromptSettingByChatbotAccountId();

    PromptStatusResp getPromptStatus();
}
