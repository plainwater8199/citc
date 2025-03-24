package com.citc.nce.im.tempStore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioAdd;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-audio 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
public interface IResourcesAudioService extends IService<ResourcesAudio> {

    void pageName(String userId, Page<ResourcesAudio> page, AudioPageQuery query);

    void add(AudioAdd add);

    List<ResourcesAudio> listByIdsDel(Collection<Long> ids);
}
