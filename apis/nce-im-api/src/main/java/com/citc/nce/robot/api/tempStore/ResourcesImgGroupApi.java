package com.citc.nce.robot.api.tempStore;

import com.citc.nce.robot.api.tempStore.bean.images.GroupAdd;
import com.citc.nce.robot.api.tempStore.bean.images.GroupInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bydud
 * @since 14:09
 */

@FeignClient(value = "im-service", contextId = "imgGroupApi", url = "${im:}")
public interface ResourcesImgGroupApi {

    @PostMapping("/tempStore/resources/imgGroup")
    @ApiOperation("新增/修改分组")
    public void addOrUpdate(@RequestBody @Validated GroupAdd add);

    @PostMapping("/tempStore/resources/imgGroup/removeByIds")
    @ApiOperation("删除分组")
    public boolean remove(@RequestBody List<Long> ids);

    @GetMapping("/tempStore/resources/imgGroup/list")
    @ApiOperation("查询分组")
    public List<GroupInfo> list();

}
