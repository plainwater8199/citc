package com.citc.nce.im.tempStore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.robot.api.tempStore.domain.ResourcesAudio;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-audio Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
public interface ResourcesAudioMapper extends BaseMapper<ResourcesAudio> {

    List<ResourcesAudio> listByIdsDel(@Param("ids") Collection<Long> ids);
}
