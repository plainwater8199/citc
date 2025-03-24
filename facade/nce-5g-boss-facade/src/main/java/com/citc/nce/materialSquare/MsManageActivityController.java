package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.vo.activity.ActivityAdd;
import com.citc.nce.materialSquare.vo.activity.ActivityDesign;
import com.citc.nce.materialSquare.vo.activity.ActivityEdit;
import com.citc.nce.materialSquare.vo.activity.ActivityPageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author bydud
 * @since 2024/5/14 16:16
 */
@RestController
@AllArgsConstructor
@Api(value = "MsManageActivityContentController", tags = "素材广场-活动页面")
public class MsManageActivityController {
    private final MsManageActivityApi msManageActivityApi;

    @ApiOperation("新增活动页面")
    @PostMapping("/msManage/activity/addActivity")
    void addActivity(@RequestBody @Valid ActivityAdd activityAdd) {
        msManageActivityApi.addActivity(activityAdd);
    }

    @ApiOperation("删除活动页面")
    @PostMapping("/msManage/activity/deleteById/{msActivityId}")
    void deleteById(@PathVariable("msActivityId") Long msActivityId) {
        msManageActivityApi.deleteById(msActivityId);
    }

    @ApiOperation("查询活动页面")
    @PostMapping("/msManage/activity/page")
    PageResult<MsManageActivity> page(@RequestBody ActivityPageQuery pageQuery) {
        return msManageActivityApi.page(pageQuery);
    }

    @ApiOperation("编辑活动页面")
    @PostMapping("/msManage/activity/editActivity")
    void editActivity(@RequestBody @Valid ActivityEdit activityEdit) {
        msManageActivityApi.editActivity(activityEdit);
    }

    @ApiOperation("设计活动页面")
    @PostMapping("/msManage/activity/designActivity")
    void designActivity(@RequestBody @Valid ActivityDesign activityDesign) {
        msManageActivityApi.designActivity(activityDesign);
    }


}
