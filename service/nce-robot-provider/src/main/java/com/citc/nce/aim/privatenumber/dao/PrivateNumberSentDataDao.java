package com.citc.nce.aim.privatenumber.dao;


import com.citc.nce.aim.privatenumber.entity.PrivateNumberSentDataDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PrivateNumberSentDataDao extends BaseMapperX<PrivateNumberSentDataDo> {
    void createTable(@Param("tableName") String tableName);
}
