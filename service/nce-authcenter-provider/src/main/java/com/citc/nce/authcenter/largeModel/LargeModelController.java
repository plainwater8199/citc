package com.citc.nce.authcenter.largeModel;

import com.citc.nce.authcenter.largeModel.service.LargeModelService;
import com.citc.nce.authcenter.largeModel.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController()
@Slf4j
public class LargeModelController implements LargeModelApi {

    @Resource
    LargeModelService largeModelService;


    @Override
    public PageResult<LargeModelResp> getLargeModelList(LargeModelReq req) {
        return largeModelService.getLargeModelList(req);
    }

    @Override
    public LargeModelResp getLargeModeDetail(Long id) {
        return largeModelService.getLargeModeDetail(id);
    }

    @Override
    public void createLargeModel(LargeModelCreateReq req) {
        largeModelService.createLargeModel(req);
    }

    @Override
    public void updateLargeModel(LargeModelUpdateReq req) {
        largeModelService.updateLargeModel(req);
    }

    @Override
    public void deleteLargeModel(LargeModelDetailReq req) {
        largeModelService.deleteLargeModel(req);
    }

    @Override
    public PageResult<PromptResp> getPromptList(PromptReq req) {
        return largeModelService.getPromptList(req);
    }

    @Override
    public PromptDetailResp getPromptDetail(Long id) {
        return largeModelService.getPromptDetail(id);
    }

    @Override
    public void updatePrompt(PromptUpdateReq req) {
        largeModelService.updatePrompt(req);
    }

    @Override
    public void updatePromptStatus(PromptUpdateStatusReq req) {
        largeModelService.updatePromptStatus(req);
    }

    @Override
    public LargeModelPromptSettingResp getLargeModelPromptSettingByChatbotAccountId() {
        return largeModelService.getLargeModelPromptSettingByChatbotAccountId();
    }

    @Override
    public PromptStatusResp getPromptStatus() {
        return largeModelService.getPromptStatus();
    }
}
