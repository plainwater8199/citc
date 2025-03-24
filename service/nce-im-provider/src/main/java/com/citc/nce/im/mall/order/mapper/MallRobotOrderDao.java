package com.citc.nce.im.mall.order.mapper;

import com.citc.nce.im.mall.order.entity.MallRobotOrderDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.entity
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  16:08
 * @Description: MybatisPlus的指令管理数据层
 * @Version: 1.0
 */
public interface MallRobotOrderDao extends BaseMapperX<MallRobotOrderDo> {
    List<MallRobotOrderDo> listByIdsDel(@Param("ids") List<Long> selectIds);
}
