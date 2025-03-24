package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.vo.activity.ContentAdd;
import com.citc.nce.materialSquare.vo.activity.ContentPageQuery;
import com.citc.nce.materialSquare.vo.activity.ContentUpdate;
import com.citc.nce.materialSquare.vo.activity.SummaryInfoForActivityContent;
import com.citc.nce.materialSquare.vo.activity.resp.MsManageActivityContentResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


/**
 * @author bydud
 * @since 2024/5/14 16:16
 */
@RestController
@AllArgsConstructor
@Api(value = "MsManageActivityContentController", tags = "素材广场-活动方案")
public class MsManageActivityContentController {
    private final MsManageActivityContentApi msManageActivityContentApi;

    @ApiOperation("新增活动方案")
    @PostMapping("msManage/activityContent/add")
    void addContent(@RequestBody @Valid ContentAdd contentAdd) {
        msManageActivityContentApi.addContent(contentAdd);
    }

    @ApiOperation("更新活动方案")
    @PostMapping("msManage/activityContent/update")
    void updateContent(@RequestBody @Valid ContentUpdate contentUpdate) {
        msManageActivityContentApi.updateContent(contentUpdate);
    }

    @ApiOperation("分页查询活动方案")
    @PostMapping("msManage/activityContent/page")
    PageResult<MsManageActivityContentResp> page(@RequestBody ContentPageQuery pageQuery) {
        return msManageActivityContentApi.page(pageQuery);
    }

    @ApiOperation("分页查询活动方案")
    @GetMapping("msManage/activityContent/get/{msActivityContentId}")
    MsManageActivityContentResp get(@PathVariable("msActivityContentId") Long msActivityContentId) {
        return msManageActivityContentApi.queryContentById(msActivityContentId);
    }

//    @PostMapping("/msManage/activityContent/getSummaryInfoForActivity/{msActivityContentId}")
//    @ApiOperation("根据关联id获取所有作品信息")
//    public List<SummaryInfoForActivityContent> getSummaryInfoForActivity(@PathVariable("msActivityContentId") Long msActivityContentId) {
//        return msManageActivityContentApi.getSummaryInfoForActivity(msActivityContentId);
//    }

}
