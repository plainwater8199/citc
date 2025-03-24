package com.citc.nce.robot.api.tempStore;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioAdd;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author bydud
 * @since 15:38
 */
@FeignClient(value = "im-service", contextId = "resourcesAudio", url = "${im:}")
public interface ResourcesAudioApi {

    @PostMapping("/tempStore/resources/audio/listByIds")
    @ApiOperation("列表")
    public List<ResourcesAudio> listByIdsDel(@RequestBody List<Long> ids);

    @PostMapping("/tempStore/resources/audio/page")
    @ApiOperation("列表")
    public PageResult<ResourcesAudio> page(@RequestBody @Validated AudioPageQuery query);

    @PostMapping("/tempStore/resources/audio")
    @ApiOperation("新增")
    public void add(@RequestBody @Validated AudioAdd add);

    @PostMapping("/tempStore/resources/audio/removeByIds")
    @ApiOperation("删除")
    public boolean remove(@RequestBody List<Long> ids);
}
