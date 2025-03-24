package com.citc.nce.authcenter.csp.customer.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CheckCspCustomerMapper {

    /**
     *
     * @param cspId cspId
     */
    Long checkCspStatusByCspId(@Param("cspId") String cspId);
}
