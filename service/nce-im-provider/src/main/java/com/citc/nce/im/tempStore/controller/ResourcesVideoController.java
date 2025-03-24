package com.citc.nce.im.tempStore.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.tempStore.domain.ResourcesVideoCovers;
import com.citc.nce.im.tempStore.service.IResourcesVideoCoversService;
import com.citc.nce.im.tempStore.service.IResourcesVideoService;
import com.citc.nce.im.tempStore.utils.PageSupport;
import com.citc.nce.robot.api.tempStore.ResourcesVideoApi;
import com.citc.nce.robot.api.tempStore.bean.video.VideoAdd;
import com.citc.nce.robot.api.tempStore.bean.video.VideoEdit;
import com.citc.nce.robot.api.tempStore.bean.video.VideoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.video.VideoQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 扩展商城-资源管理-视频 前端控制器
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
@RestController
@AllArgsConstructor
public class ResourcesVideoController implements ResourcesVideoApi {
    private IResourcesVideoService videoService;
    private final IResourcesVideoCoversService coversService;

    private void setupVideoCovers(List<ResourcesVideo> videos){
        for (ResourcesVideo video : videos) {
            List<String> covers = coversService.lambdaQuery()
                    .eq(ResourcesVideoCovers::getVideoId, video.getVideoId())
                    .list()
                    .stream()
                    .map(ResourcesVideoCovers::getCover)
                    .collect(Collectors.toList());
            video.setCovers(covers);
        }
    }

    private void setupVideoCropObj(List<ResourcesVideo> videos){

    }

    @Override
    public List<ResourcesVideo> listByIdsDel(List<Long> ids) {
        List<ResourcesVideo> list = videoService.listByIdsDel(ids);
        setupVideoCovers(list);
        return list;
    }

    @Override
    public PageResult<ResourcesVideo> page(VideoPageQuery query) {
        Page<ResourcesVideo> page = PageSupport.getPage(ResourcesVideo.class, query.getPageNo(), query.getPageSize());
        videoService.pageName(SessionContextUtil.verifyCspLogin(),page,query);
        setupVideoCovers(page.getRecords());
        return new PageResult<>(page.getRecords(),page.getTotal());
    }

    @Override
    public void add(VideoAdd add) {
        videoService.add(add);
    }

    @Override
    public void update(VideoEdit edit) {
        videoService.edit(edit);
    }

    @Override
    public boolean remove(List<Long> ids) {
        List<ResourcesVideo> videos = videoService.listByIds(ids);
        videos.forEach(s->SessionContextUtil.sameCsp(s.getCreator()));
        return videoService.removeBatchByIds(ids);
    }

    @Override
    public VideoQuery getById(Long videoId) {
        ResourcesVideo video = videoService.getById(videoId);
        if (Objects.isNull(video)) {
            throw new BizException("视频已删除");
        }
        SessionContextUtil.sameCsp(video.getCreator());
        return videoService.queryOne(videoId);
    }
}

