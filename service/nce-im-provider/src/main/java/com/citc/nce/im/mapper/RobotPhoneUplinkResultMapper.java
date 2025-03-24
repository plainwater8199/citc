package com.citc.nce.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.RobotPhoneUplinkResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RobotPhoneUplinkResultMapper extends BaseMapper<RobotPhoneUplinkResult> {
    List<String> selectAnyClick(@Param("detailId") Long detailId, @Param("nowStr") String nowStr);
}
