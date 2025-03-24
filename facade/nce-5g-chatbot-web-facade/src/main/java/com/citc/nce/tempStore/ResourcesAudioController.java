package com.citc.nce.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.ResourcesAudioApi;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioAdd;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bydud
 * @since 15:47
 */
@RestController
@Api(tags = "csp-模板商城-资源管理-audio管理")
@AllArgsConstructor
public class ResourcesAudioController {

    private final ResourcesAudioApi audioApi;

    @PostMapping("/tempStore/resources/audio/listByIds")
    @ApiOperation("listByIds")
    public List<ResourcesAudio> listByIds(@RequestBody List<Long> ids) {
        return audioApi.listByIdsDel(ids);
    }

    @PostMapping("/tempStore/resources/audio/page")
    @ApiOperation("列表")
    public PageResult<ResourcesAudio> page(@RequestBody @Validated AudioPageQuery query) {
        return audioApi.page(query);
    }

    @PostMapping("/tempStore/resources/audio")
    @ApiOperation("新增")
    public void add(@RequestBody @Validated AudioAdd add) {
        audioApi.add(add);
    }

    @PostMapping("/tempStore/resources/audio/removeByIds")
    @ApiOperation("删除")
    public boolean remove(@RequestBody List<Long> ids) {
        return audioApi.remove(ids);
    }
}
