package com.citc.nce.materialSquare.controller;

import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.MsManageActivityContentApi;
import com.citc.nce.materialSquare.PromotionType;
import com.citc.nce.materialSquare.service.IMsManageActivityContentService;
import com.citc.nce.materialSquare.service.IMsManageActivityLiService;
import com.citc.nce.materialSquare.vo.activity.*;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 * <p>
 * 素材广场_后台管理_活动方案 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@RestController
@Slf4j
public class MsManageActivityContentController implements MsManageActivityContentApi {

    @Resource
    private IMsManageActivityContentService contentService;

    @Resource
    private IMsManageActivityLiService msManageActivityLiService;

    @Override
    public void addContent(@Valid ContentAdd contentAdd) {
        check(contentAdd);
        contentService.addContent(contentAdd);
    }

    @Override
    public void updateContent(@Valid ContentUpdate contentUpdate) {
        check(contentUpdate);
        contentService.updateContent(contentUpdate);
    }

    @Override
    public PageResult<MsManageActivityContentResp> page(ContentPageQuery pageQuery) {
        Map<Long, Integer> countMap = msManageActivityLiService.queryCountForContentId();
        return contentService.pageContent(pageQuery,countMap);
    }

    @Override
    public MsManageActivityContentResp queryContentById(Long msActivityContentId) {
        return contentService.queryContentById(msActivityContentId);
    }

    @Override
    public MsPrice getPrice(@RequestParam("mssId") Long mssId, @RequestParam(name = "msActivityContentId",required = false)Long msActivityContentId, @RequestParam(name ="pType",required = false) String pType) {
        return contentService.getPrice(mssId,msActivityContentId,pType);
    }

    @Override
    public List<MsManageActivityContentResp> list() {
        return contentService.activityContentlist();
    }

    private void check(ContentAdd content) {
        if (content.getStartTime().after(content.getEndTime())) {
            throw new BizException("有效期开始时间不能小于结束时间");
        }
        if (content.getStartTime().before(new Date())) {
            throw new BizException("有效期开始时间不能小于当前时间");
        }
        if (PromotionType.DISCOUNT.equals(content.getPromotionType())) {
            if (Objects.isNull(content.getDiscountRate())) {
                throw new BizException("折扣系数不能为空");
            }
            if (content.getDiscountRate() <= 0) {
                throw new BizException("折扣系数不能小于0");
            }
            if (content.getDiscountRate() >= 1) {
                throw new BizException("折扣系数不能大于或等于1");
            }
        }
    }
}

