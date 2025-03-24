package com.citc.nce.im.tempStore.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.api.tempStore.bean.video.VideoAdd;
import com.citc.nce.robot.api.tempStore.bean.video.VideoEdit;
import com.citc.nce.robot.api.tempStore.bean.video.VideoPageQuery;
import com.citc.nce.robot.api.tempStore.bean.video.VideoQuery;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-视频 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
public interface IResourcesVideoService extends IService<ResourcesVideo> {

    /**
     * 分页查询
     *
     * @param page  分页参数
     * @param query 查询参数
     */
    void pageName(String userId, Page<ResourcesVideo> page, VideoPageQuery query);

    void add(VideoAdd add);

    void edit(VideoEdit edit);

    /**
     * 查询视频的详细信息
     *
     * @param videoId 视频id
     * @return
     */
    VideoQuery queryOne(Long videoId);

    List<ResourcesVideo> listByIdsDel(Collection<Long> ids);
}
