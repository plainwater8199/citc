package com.citc.nce.aim.dao;


import com.citc.nce.aim.entity.AimSentDataDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AimSentDataDao extends BaseMapperX<AimSentDataDo> {
    void createTable(@Param("tableName") String tableName);
    List<String> findSendTimeListByProjectIdAndTime(@Param("projectId")String projectId, @Param("startDate")String startDate, @Param("endDate")String endDate);
}
