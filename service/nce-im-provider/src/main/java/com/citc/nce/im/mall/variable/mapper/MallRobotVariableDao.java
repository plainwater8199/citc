package com.citc.nce.im.mall.variable.mapper;

import com.citc.nce.im.mall.variable.entity.MallRobotVariableDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MallRobotVariableDao extends BaseMapperX<MallRobotVariableDo> {
    List<MallRobotVariableDo> listByIdsDel(@Param("ids") List<Long> selectIds);

    List<MallRobotVariableDo> listByIds(@Param("ids") List<Long> selectIds);
}
