package com.citc.nce.im.tempStore.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.service.IResourcesAudioService;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.robot.api.tempStore.ResourcesAudioApi;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioAdd;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-audio 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
@RestController
@AllArgsConstructor
public class ResourcesAudioController implements ResourcesAudioApi {
    private IResourcesAudioService audioService;

    @Override
    public List<ResourcesAudio> listByIdsDel(List<Long> ids) {
        return audioService.listByIdsDel(ids);
    }

    @Override
    public PageResult<ResourcesAudio> page(AudioPageQuery query) {
        Page<ResourcesAudio> page = PageSupport.getPage(ResourcesAudio.class, query.getPageNo(), query.getPageSize());
        audioService.pageName(SessionContextUtil.verifyCspLogin(), page, query);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Override
    public void add(AudioAdd add) {
        audioService.add(add);
    }

    @Override
    public boolean remove(List<Long> ids) {
        List<ResourcesAudio> audios = audioService.listByIds(ids);
        audios.forEach(s->SessionContextUtil.sameCsp(s.getCreator()));
        return audioService.removeBatchByIds(ids);
    }
}

