package com.citc.nce.authcenter.tenantdata.user.dao;


import com.citc.nce.authcenter.tenantdata.user.entity.CspCustomerDo;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiancheng
 */
//@DS("user")
public interface CspCustomer1Dao extends BaseMapperX<CspCustomerDo> {


    void dropTable(@Param("tableName") String tableName);

    List<CspCustomerDo> selectListByName(@Param("tableName") String tableName);

    List<CspCustomerDo> queryDeletedList();

    void updateByCustomerId(@Param("customerId")String customerId);
}
