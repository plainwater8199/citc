package com.citc.nce.im.tempStore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.robot.api.tempStore.domain.ResourcesVideo;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-视频 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:54
 */
public interface ResourcesVideoMapper extends BaseMapper<ResourcesVideo> {

    /**
     * list包括删除
     *
     * @param ids ids
     */
    List<ResourcesVideo> listByIdsDel(@Param("ids") Collection<Long> ids);
}
