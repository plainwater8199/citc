package com.citc.nce.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.ResourcesImgApi;
import com.citc.nce.robot.api.tempStore.ResourcesImgGroupApi;
import com.citc.nce.robot.api.tempStore.bean.images.*;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bydud
 * @since 15:27
 */

@RestController
@Api(tags = "csp-模板商城-资源管理-img管理")
@AllArgsConstructor
public class ResourcesImgController {
    private final ResourcesImgGroupApi groupApi;
    private final ResourcesImgApi imgApi;


    @PostMapping("/tempStore/resources/imgGroup")
    @ApiOperation("新增/修改分组")
    public void addOrUpdate(@RequestBody @Validated GroupAdd add) {
        groupApi.addOrUpdate(add);
    }

    @PostMapping("/tempStore/resources/imgGroup/removeByIds")
    @ApiOperation("删除分组")
    public boolean remove(@RequestBody List<Long> ids) {
        return groupApi.remove(ids);
    }

    @GetMapping("/tempStore/resources/imgGroup/list")
    @ApiOperation("查询分组")
    public List<GroupInfo> list() {
        return groupApi.list();
    }

//===========================分割线

    @PostMapping("/tempStore/resources/img/listByIds")
    @ApiOperation("listByIds")
    public List<ResourcesImg> listByIds(@RequestBody List<Long> ids) {
        return imgApi.listByIdsDel(ids);
    }

    @PostMapping("/tempStore/resources/img/page")
    @ApiOperation("列表")
    public PageResult<ResourcesImg> page(@RequestBody @Validated ImgPageQuery query) {
        return imgApi.page(query);
    }

    @PostMapping("/tempStore/resources/img")
    @ApiOperation("新增")
    public void add(@RequestBody @Validated ImgAdd add) {
        imgApi.add(add);
    }

    @PostMapping("/tempStore/resources/img/removeByIds")
    @ApiOperation("删除")
    public boolean removeImg(@RequestBody List<Long> ids) {
        return imgApi.remove(ids);
    }

    @PostMapping("/tempStore/resources/img/moveImg")
    @ApiOperation("移动")
    public void moveImg(@RequestBody @Validated ImgMove move) {
        imgApi.moveImg(move);
    }
}
