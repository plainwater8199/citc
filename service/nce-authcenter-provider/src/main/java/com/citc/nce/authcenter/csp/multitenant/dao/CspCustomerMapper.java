package com.citc.nce.authcenter.csp.multitenant.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import com.citc.nce.authcenter.csp.vo.*;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author jiancheng
 */
public interface CspCustomerMapper extends BaseMapperX<CspCustomer> {

    void addEnableAgentLogin();
    void deleteEnableAgentLogin();

    void addBalanceField();
    void addPayTypeColum();

    void deletePayTypeColum();

    List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(@Param("cspId") String cspId,@Param("payType") Integer payType);
    /**
     * 用户名查客户
     *
     * @param tableName 表名称 csp_customer_+ cspId
     * @param name      账号
     */
    CspCustomer queryCspCustomerByAccount(@Param("tableName") String tableName, @Param("name") String name);

    /**
     * customerId查客户
     *
     * @param tableName 表名称 csp_customer_+ cspId
     * @param customerId 客户ID
     */
    CspCustomer queryCspCustomerByCustomerId(@Param("tableName") String tableName, @Param("customerId") String customerId);

    /**
     * 用户名查客户
     *
     * @param tableName 表名称 csp_customer_+ cspId
     * @param phone     手机号
     */
    CspCustomer queryCspCustomerByPhone(@Param("tableName") String tableName, @Param("phone") String phone);

    Page<CustomerSearchResp> queryCspCustomerList(
            @Param("enterpriseAccountName") String enterpriseAccountName,
            @Param("customerActive") Integer customerActive,
            @Param("phone") String phone,
            @Param("cspId") String cspId,
            @Param("javaNow") Date date,
            Page<CustomerSearchResp> page);

    List<CustomerProvinceResp> getCustomerDistribution(@Param("cspId") String cspId);

    CustomerDetailResp getCustomerDetail(@Param("customerId") String customerId);

    Page<CustomerOptionVo> getCustomerOption(
            @Param("operatorCode") Integer operatorCode,
            @Param("name") String name,
            @Param("cspId") String cspId,
            Page<CustomerOptionVo> page);

    void addColumOutOfTime();

    void setCustomerToFormal(@Param("customerId") String customerId);

    void removeByCustomerId(@Param("customerId") String customerId,@Param("updater") String updater);
}
