package com.citc.nce.auth.csp.contract.dao;

import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.contract.vo.ContractReq;
import com.citc.nce.auth.csp.contract.vo.ContractResp;
import com.citc.nce.auth.csp.contract.vo.ContractServiceCodeReq;
import com.citc.nce.auth.csp.statistics.vo.CspStatisticsIndustryTypeResp;
import com.citc.nce.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.dao
 * @Author: litao
 * @CreateTime: 2023-02-13  10:11

 * @Version: 1.0
 */
@Mapper
public interface ContractManageDao extends BaseMapperX<ContractManageDo> {
    List<ContractResp> queryList(ContractReq req);

    Long queryListCount(ContractReq req);

    Integer cleanServiceCode(ContractServiceCodeReq req);

    List<CspStatisticsIndustryTypeResp> getContractIndustryType();
}
