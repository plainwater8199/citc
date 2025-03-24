package com.citc.nce.im.tempStore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.robot.api.tempStore.domain.ResourcesImg;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 扩展商城-资源管理-图片-分组管理 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 11:11:28
 */
public interface ResourcesImgMapper extends BaseMapper<ResourcesImg> {

    List<ResourcesImg> listByIdsDel(@Param("ids") Collection<Long> ids);
}
