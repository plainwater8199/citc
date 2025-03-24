package com.citc.nce.im.tempStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.mapper.ResourcesAudioMapper;
import com.citc.nce.im.tempStore.service.IResourcesAudioService;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioAdd;
import com.citc.nce.robot.api.tempStore.bean.audio.AudioPageQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 扩展商城-资源管理-audio 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
@Service
public class ResourcesAudioServiceImpl extends ServiceImpl<ResourcesAudioMapper, ResourcesAudio> implements IResourcesAudioService {
    @Override
    public void pageName(String userId, Page<ResourcesAudio> page, AudioPageQuery query) {
        LambdaQueryWrapper<ResourcesAudio> like = new LambdaQueryWrapper<ResourcesAudio>()
                .eq(ResourcesAudio::getCreator, userId)
                .like(StringUtils.hasLength(query.getName()), ResourcesAudio::getName, query.getName())
                .orderByDesc(ResourcesAudio::getCreateTime);
        page(page, like);
    }

    @Override
    public void add(AudioAdd add) {
        ResourcesAudio one = getOne(new LambdaUpdateWrapper<ResourcesAudio>()
                .eq(ResourcesAudio::getName, add.getName())
                .eq(ResourcesAudio::getCreator, SessionContextUtil.verifyCspLogin())
        );
        if (Objects.nonNull(one)) {
            throw new BizException("名称不能重复");
        }
        ResourcesAudio resourcesAudio = new ResourcesAudio();
        BeanUtils.copyProperties(add, resourcesAudio);
        save(resourcesAudio);
    }

    @Override
    public List<ResourcesAudio> listByIdsDel(Collection<Long> ids) {
        return getBaseMapper().listByIdsDel(ids);
    }
}
