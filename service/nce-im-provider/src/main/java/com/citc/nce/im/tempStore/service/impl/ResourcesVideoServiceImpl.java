package com.citc.nce.im.tempStore.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.JsonUtils;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.domain.ResourcesVideoCovers;
import com.citc.nce.im.tempStore.mapper.ResourcesVideoMapper;
import com.citc.nce.im.tempStore.service.IResourcesVideoCoversService;
import com.citc.nce.im.tempStore.service.IResourcesVideoService;
import com.citc.nce.robot.api.tempStore.bean.video.VideoAdd;
import com.citc.nce.robot.api.tempStore.bean.video.VideoEdit;
import com.citc.nce.robot.api.tempStore.bean.video.VideoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.video.VideoQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 扩展商城-资源管理-视频 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
@Service
@AllArgsConstructor
public class ResourcesVideoServiceImpl extends ServiceImpl<ResourcesVideoMapper, ResourcesVideo> implements IResourcesVideoService {
    private final IResourcesVideoCoversService coversService;

    @Override
    public void pageName(String userId, Page<ResourcesVideo> page, VideoPageQuery query) {
        LambdaUpdateWrapper<ResourcesVideo> like = new LambdaUpdateWrapper<ResourcesVideo>()
                .eq(ResourcesVideo::getCreator, userId)
                .like(StringUtils.hasLength(query.getName()), ResourcesVideo::getName, query.getName())
                .orderByDesc(ResourcesVideo::getCreateTime);
        page(page, like);
    }

    @Override
    @Transactional
    public void add(VideoAdd add) {
        checkName(add.getName(), null);
        ResourcesVideo resourcesVideo = new ResourcesVideo();
        BeanUtils.copyProperties(add, resourcesVideo);
        resourcesVideo.setCover(add.getMainCoverId());
        resourcesVideo.setCropObj(JsonUtils.obj2String(add.getCropObj()));
        save(resourcesVideo);
        List<String> covers = add.getCovers();
        if (CollectionUtils.isEmpty(covers)) return;
        List<ResourcesVideoCovers> list = new ArrayList<>(5);
        for (String cover : covers) {
            ResourcesVideoCovers videoCovers = new ResourcesVideoCovers();
            videoCovers.setVideoId(resourcesVideo.getVideoId());
            videoCovers.setCover(cover);
            list.add(videoCovers);
        }
        coversService.saveBatch(list);
    }

    private void checkName(String name, Long id) {
        boolean exists = exists(new LambdaUpdateWrapper<ResourcesVideo>()
                .eq(ResourcesVideo::getName, name)
                .ne(Objects.nonNull(id), ResourcesVideo::getVideoId, id)
                .eq(ResourcesVideo::getCreator, SessionContextUtil.verifyCspLogin())
        );
        if (exists) {
            throw new BizException("名称不能重复");
        }
    }

    @Override
    public void edit(VideoEdit edit) {
        checkName(edit.getName(), edit.getVideoId());
        ResourcesVideo video = getById(edit.getVideoId());
        if (Objects.isNull(video)) {
            throw new BizException("视频已删除");
        }
        SessionContextUtil.sameCsp(video.getCreator());
        ResourcesVideo resourcesVideo = new ResourcesVideo();
        BeanUtils.copyProperties(edit, resourcesVideo);
        updateById(resourcesVideo);
    }

    @Override
    public VideoQuery queryOne(Long videoId) {
        ResourcesVideo video = getById(videoId);
        if (Objects.isNull(video)) {
            throw new BizException("视频已删除");
        }
        List<String> covers = coversService.listCoversByVideoId(videoId);
        VideoQuery videoQuery = new VideoQuery();
        BeanUtils.copyProperties(video, videoQuery);
        videoQuery.setCovers(covers);
        return videoQuery;
    }

    @Override
    public List<ResourcesVideo> listByIdsDel(Collection<Long> ids) {
        return getBaseMapper().listByIdsDel(ids);
    }
}
