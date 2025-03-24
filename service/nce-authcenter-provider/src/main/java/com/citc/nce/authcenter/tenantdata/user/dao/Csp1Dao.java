package com.citc.nce.authcenter.tenantdata.user.dao;


import com.citc.nce.authcenter.tenantdata.user.entity.CspDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jiancheng
 */
public interface Csp1Dao extends BaseMapperX<CspDo> {
    Long selectCountByName(@Param("tableName") String tableName);


    @MapKey("")
    Map<String, String> checkTableIsExistByName(@Param("tableName") String tableName);

    List<String> queryAllCspId();

    List<String> queryAllCspCustomerId(@Param("cspId") String cspId);
}
