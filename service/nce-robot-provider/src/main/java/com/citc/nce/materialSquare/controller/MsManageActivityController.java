package com.citc.nce.materialSquare.controller;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.materialSquare.MsManageActivityApi;
import com.citc.nce.materialSquare.entity.MsManageActivity;
import com.citc.nce.materialSquare.service.IMsManageActivityService;
import com.citc.nce.materialSquare.vo.activity.ActivityAdd;
import com.citc.nce.materialSquare.vo.activity.ActivityDesign;
import com.citc.nce.materialSquare.vo.activity.ActivityEdit;
import com.citc.nce.materialSquare.vo.activity.ActivityPageQuery;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 素材广场_后台管理_活动封面	 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@RestController
@AllArgsConstructor
public class MsManageActivityController implements MsManageActivityApi {

    private final IMsManageActivityService activityService;

    @Override
    public void addActivity(@RequestBody @Valid ActivityAdd activityAdd) {
        activityService.addActivity(activityAdd);
    }

    @Override
    public void deleteById(@PathVariable("msActivityId") Long msActivityId) {
        activityService.deleteById(msActivityId);
    }

    @Override
    public PageResult<MsManageActivity> page(@RequestBody ActivityPageQuery pageQuery) {
        return activityService.pageResult(pageQuery);
    }

    @Override
    public void editActivity(@RequestBody @Valid ActivityEdit activityEdit) {
        activityService.editActivity(activityEdit);
    }

    @Override
    public void designActivity(@RequestBody @Valid ActivityDesign activityDesign) {
        activityService.designActivity(activityDesign);
    }
}

