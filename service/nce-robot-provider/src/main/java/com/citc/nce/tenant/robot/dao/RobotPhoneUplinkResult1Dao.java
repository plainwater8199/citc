package com.citc.nce.tenant.robot.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.tenant.robot.entity.RobotPhoneUplinkResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RobotPhoneUplinkResult1Dao extends BaseMapper<RobotPhoneUplinkResult> {
    List<String> selectAnyClick(@Param("detailId") Long detailId, @Param("nowStr") String nowStr);
}
