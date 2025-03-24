package com.citc.nce.im.tempStore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.im.tempStore.domain.ResourcesVideoCovers;

import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:51
 */
public interface IResourcesVideoCoversService extends IService<ResourcesVideoCovers> {
    /**
     * 查询视频的封面图
     *
     * @param videoId 视频id
     * @return 封面图fileId
     */
    List<String> listCoversByVideoId(Long videoId);
}
