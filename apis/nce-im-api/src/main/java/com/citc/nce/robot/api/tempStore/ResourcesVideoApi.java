package com.citc.nce.robot.api.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.bean.video.VideoAdd;
import com.citc.nce.robot.api.tempStore.bean.video.VideoEdit;
import com.citc.nce.robot.api.tempStore.bean.video.VideoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.video.VideoQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bydud
 * @since 15:09
 */
@FeignClient(value = "im-service", contextId = "resourcesImgVideoApi", url = "${im:}")
public interface ResourcesVideoApi {

    /**
     * 包括删除
     */
    @PostMapping("/tempStore/resources/video/listByIdsDel")
    @ApiOperation("listByIds")
    public List<ResourcesVideo> listByIdsDel(@RequestBody List<Long> ids);

    @PostMapping("/tempStore/resources/video/page")
    @ApiOperation("列表")
    public PageResult<ResourcesVideo> page(@RequestBody @Validated VideoPageQuery query);
    @PostMapping("/tempStore/resources/video")
    @ApiOperation("新增图片")
    public void add(@RequestBody @Validated VideoAdd add);
    @PostMapping("/tempStore/resources/video/update")
    @ApiOperation("修改图片名称和封面")
    public void update(@RequestBody @Validated VideoEdit edit);
    @PostMapping("/tempStore/resources/video/removeByIds")
    @ApiOperation("删除图片")
    public boolean remove(@RequestBody List<Long> ids);
    @GetMapping("/tempStore/resources/video/getById/{videoId}")
    @ApiOperation("getById")
    public VideoQuery getById(@PathVariable("videoId") Long videoId);
}
