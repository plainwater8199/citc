package com.citc.nce.robot.api.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.bean.images.ImgAdd;
import com.citc.nce.robot.api.tempStore.bean.images.ImgMove;
import com.citc.nce.robot.api.tempStore.bean.images.ImgPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bydud
 * @since 11:55
 */
@FeignClient(value = "im-service", contextId = "resourcesImgApi", url = "${im:}")
public interface ResourcesImgApi {

    @PostMapping("/tempStore/resources/img/listByIdsDel")
    @ApiOperation("listByIdsDel")
    public List<ResourcesImg> listByIdsDel(@RequestBody List<Long> ids);

    @PostMapping("/tempStore/resources/img/page")
    @ApiOperation("列表")
    public PageResult<ResourcesImg> page(@RequestBody @Validated ImgPageQuery query);

    @PostMapping("/tempStore/resources/img")
    @ApiOperation("新增图片")
    public void add(@RequestBody @Validated ImgAdd add);

    @PostMapping("/tempStore/resources/img/removeByIds")
    @ApiOperation("删除图片")
    public boolean remove(@RequestBody List<Long> ids);

    @PostMapping("/tempStore/resources/img/moveImg")
    @ApiOperation("移动图片")
    public void moveImg(@RequestBody @Validated ImgMove move);
}

