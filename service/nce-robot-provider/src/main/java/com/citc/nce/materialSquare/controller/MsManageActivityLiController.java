package com.citc.nce.materialSquare.controller;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.MsManageActivityLiApi;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import com.citc.nce.materialSquare.service.IMsManageActivityLiService;
import com.citc.nce.materialSquare.vo.activity.*;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 素材广场_后台管理_参与活动的素材 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:36
 */
@RestController
@AllArgsConstructor
public class MsManageActivityLiController implements MsManageActivityLiApi {

    private final IMsManageActivityLiService liService;

    @Override
    public PageResult<LiPageResult> liPage(@RequestBody LiPageQuery pageQuery) {
        return liService.liPage(pageQuery);
    }

    @Override
    public void putAndRemove(@RequestBody @Valid PutAndRemove putAndRemove) {
        liService.addLi(putAndRemove);
    }

    @Override
    public void delete(Long msActivityLiId) {
        liService.removeById(msActivityLiId);
    }

    @Override
    public MsManageActivityContent getActivityContentByMssId(@PathVariable("mssId") Long mssId) {
        return liService.getActivityContentByMssId(mssId);
    }

    @Override
    public List<SummaryInfoForActivityContent> getSummaryInfoForActivity(Long msActivityContentId) {
        return liService.getSummaryInfoForActivity(msActivityContentId);
    }

    @Override
    public List<LiDetailResult> listByLiId(MssForLiReq req) {
        return liService.listByLiId(req);
    }

    @Override
    public void deleteSummaryForActivity(List<Long> mssIds) {
        liService.deleteSummaryForActivity(mssIds);
    }

    @Override
    public MsManageActivityContent getActivityContentById(@PathVariable("mssId") Long msActivityContentId) {
        return liService.getActivityContentById(msActivityContentId);
    }
}

