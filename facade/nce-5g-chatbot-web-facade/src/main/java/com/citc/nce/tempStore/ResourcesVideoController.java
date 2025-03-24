package com.citc.nce.tempStore;


import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.ResourcesVideoApi;
import com.citc.nce.robot.api.tempStore.bean.video.*;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 扩展商城-资源管理-视频 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
@RestController
@Api(tags = "csp-模板商城-资源管理-video管理")
@AllArgsConstructor
public class ResourcesVideoController {
    private ResourcesVideoApi videoApi;

    @PostMapping("/tempStore/resources/video/listByIds")
    @ApiOperation("listByIds")
    public List<ResourcesVideo> listByIds(@RequestBody List<Long> ids) {
        return videoApi.listByIdsDel(ids);
    }

    @PostMapping("/tempStore/resources/video/page")
    @ApiOperation("列表")
    public PageResult<ResourcesVideo> page(@RequestBody @Validated VideoPageQuery query) {
        return videoApi.page(query);
    }

    @PostMapping("/tempStore/resources/video")
    @ApiOperation("新增")
    public void add(@RequestBody @Validated VideoAdd add) {
        videoApi.add(add);
    }

    @PostMapping("/tempStore/resources/video/update")
    @ApiOperation("修改")
    public void update(@RequestBody @Validated VideoEdit edit) {
        videoApi.update(edit);
    }

    @PostMapping("/tempStore/resources/video/removeByIds")
    @ApiOperation("删除")
    public boolean remove(@RequestBody List<Long> ids) {
        return videoApi.remove(ids);
    }


    @GetMapping("/tempStore/resources/video/getById/{videoId}")
    @ApiOperation("getById")
    public VideoQuery getById(@PathVariable("videoId") Long videoId) {
        if (Objects.isNull(videoId)) {
            throw new BizException("使用id不能为空");
        }
        return videoApi.getById(videoId);
    }

}

