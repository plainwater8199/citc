package com.citc.nce.materialSquare;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.vo.activity.ActivityAdd;
import com.citc.nce.materialSquare.vo.activity.ActivityDesign;
import com.citc.nce.materialSquare.vo.activity.ActivityEdit;
import com.citc.nce.materialSquare.vo.activity.ActivityPageQuery;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表
 * </p>
 *
 * @author
 * @since 2024/5/14 14:21
 */
@FeignClient(value = "rebot-service", contextId = "MsManageActivityApi", url = "${robot:}")
public interface MsManageActivityApi {

    @ApiOperation("新增活动页面")
    @PostMapping("/msManage/activity/addActivity")
    void addActivity(@RequestBody @Valid ActivityAdd activityAdd);

    @ApiOperation("删除活动页面")
    @PostMapping("/msManage/activity/deleteById/{msActivityId}")
    void deleteById(@PathVariable("msActivityId") Long msActivityId);

    @ApiOperation("分页查询活动页面")
    @PostMapping("/msManage/activity/page")
    PageResult<MsManageActivity> page(@RequestBody ActivityPageQuery pageQuery);

    @ApiOperation("编辑活动页面")
    @PostMapping("/msManage/activity/editActivity")
    void editActivity(@RequestBody @Valid ActivityEdit activityEdit);

    @ApiOperation("设计活动页面")
    @PostMapping("/msManage/activity/designActivity")
    void designActivity(@RequestBody @Valid ActivityDesign activityDesign);
}
