package com.citc.nce.materialSquare.controller;


import com.citc.nce.materialSquare.MsManageSuggestApi;
import com.citc.nce.materialSquare.service.IMsManageSuggestService;
import com.citc.nce.materialSquare.vo.suggest.SuggestAdd;
import com.citc.nce.materialSquare.vo.suggest.SuggestChangeNum;
import com.citc.nce.materialSquare.vo.suggest.SuggestListOrderNum;
import com.citc.nce.materialSquare.vo.suggest.req.SuggestOrderReq;
import com.citc.nce.materialSquare.vo.suggest.resp.SuggestListResp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 素材广场_后台管理_首页推荐	 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-15 10:05:47
 */
@RestController
@Slf4j
@AllArgsConstructor
public class MsManageSuggestController implements MsManageSuggestApi {

    private final IMsManageSuggestService suggestService;

    @Override
    public void putSuggest(SuggestAdd suggestAdd) {
        suggestService.putSuggest(suggestAdd);
    }

    @Override
    public void deleteSuggest(Long msSuggestId) {
        suggestService.removeById(msSuggestId);
    }

    @Override
    public void orderNum(@RequestBody @Valid SuggestOrderReq req) {
        suggestService.changeOrderNum(req);
    }

    @Override
    public SuggestListResp listOrderNum() {
        return suggestService.listOrderNum();
    }

    @Override
    public void setTop(SuggestAdd suggestAdd) {
        suggestService.setTop(suggestAdd);
    }

    @Override
    public void cleanTop() {
        suggestService.cleanTop();
    }

}

