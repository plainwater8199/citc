package com.citc.nce.im.tempStore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.robot.api.tempStore.domain.ResourcesForm;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城—资源管理-表单管理 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-23 10:11:59
 */
public interface ResourcesFormMapper extends BaseMapper<ResourcesForm> {

    List<ResourcesForm> listByIdsDel(@Param("ids") Collection<Long> ids);
}
