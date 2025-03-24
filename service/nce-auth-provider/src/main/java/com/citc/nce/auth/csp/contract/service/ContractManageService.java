package com.citc.nce.auth.csp.contract.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.contract.vo.*;
import com.citc.nce.common.core.pojo.PageResult;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.contract.service
 * @Author: litao
 * @CreateTime: 2023-02-13  10:21
 
 * @Version: 1.0
 */
public interface ContractManageService extends IService<ContractManageDo> {

    public PageResult<ContractResp> queryList(ContractReq req);

    public int setServiceCode(ContractServiceCodeReq req);

    public int cleanServiceCode(ContractServiceCodeReq req);

    public int saveOrUpdate(ContractSaveReq req);

    public ContractDetailResp getDetailById(ContractDetailReq req);

    int updateContractStatusByContractId(Long contractId, int status);

    /**
     * 新增移动合同
     */
    void addContractCmcc(ContractCmccAdd req);

    /**
     * 变更移动合同
     */
    void upgradeCmcc(ContractCmccUpgrade req);

    ContractCmccInfo getCmccContract(Long id);

    /**
     * 新增电信联通合同
     */
    void saveUniTel(ContractUTAdd req,Integer isp);


    /**
     * 变更电信联通合同
     */
    void upgradeUniTel(ContractUTUpgrade req,Integer isp);


    ContractUTInfo getUnTelContract(Long id);

    ContractCmccInfo getCmccContractChange(Long id);

    ContractUTInfo getUnTelContractChange(Long id);

    void saveSupplierContract(ContractSupplierAdd req);

    void updateSupplierContract(ContractSupplierUpdate req);

    ContractSupplierInfo getSupplierContractInfo(Long id);

    void deleteSupplierContract(Long id);

    void logOffSupplierContract(Long id);

    ContractStatusResp getContractStatusOptions();

    Boolean getNewContractPermission(ContractNewPermissionReq req);

    Boolean checkForCanCreate(ContractNewPermissionReq req);
    
}
