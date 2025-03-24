package com.citc.nce.im.tempStore.service.impl;

import com.citc.nce.im.tempStore.domain.ResourcesVideoCovers;
import com.citc.nce.im.tempStore.mapper.ResourcesVideoCoversMapper;
import com.citc.nce.im.tempStore.service.IResourcesVideoCoversService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 扩展商城-资源管理-视频-封面资源表 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:51
 */
@Service
public class ResourcesVideoCoversServiceImpl extends ServiceImpl<ResourcesVideoCoversMapper, ResourcesVideoCovers> implements IResourcesVideoCoversService {
    @Override
    public List<String> listCoversByVideoId(Long videoId) {
        return lambdaQuery().eq(ResourcesVideoCovers::getVideoId, videoId).list()
                .stream().map(ResourcesVideoCovers::getCover).collect(Collectors.toList());
    }
}
