package com.citc.nce.auth.csp.contract;

import com.citc.nce.auth.csp.contract.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.core.pojo.RestResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:47
 */
@FeignClient(value = "auth-service", contextId = "CSPContract", url = "${auth:}")
public interface ContractApi {
    /**
     * 列表查询
     *
     * @param req
     * @return list
     */
    @PostMapping("/csp/contract/queryList")
    PageResult<ContractResp> queryList(@RequestBody @Valid ContractReq req);

    @PostMapping("/csp/contract/setServiceCode")
    int setServiceCode(@RequestBody @Valid ContractServiceCodeReq req);

    @PostMapping("/csp/contract/cleanServiceCode")
    int cleanServiceCode(@RequestBody @Valid ContractServiceCodeReq req);

    @PostMapping("/csp/contract/saveOrUpdate")
    int saveOrUpdate(@RequestBody @Valid ContractSaveReq req);

    @PostMapping("/csp/contract/getDetailById")
    ContractDetailResp getDetailById(@RequestBody @Valid ContractDetailReq req);


    @PostMapping("/csp/contract/save/cmcc")
    @ApiOperation(value = "移动合同新增")
    void saveCmcc(@RequestBody @Valid ContractCmccAdd req);

    @PostMapping("/csp/contract/upgrade/cmcc")
    @ApiOperation(value = "移动合同变更")
    void upgradeCmcc(@RequestBody @Valid ContractCmccUpgrade req);

    @GetMapping("/csp/contract/getInfo/cmcc/{id}")
    @ApiOperation(value = "移动合同具体信息")
    ContractCmccInfo getCmccContract(@PathVariable("id") Long id);

    @GetMapping("/csp/contract/getInfo/cmccChange/{id}")
    @ApiOperation(value = "移动合同变更具体信息")
    ContractCmccInfo getCmccContractChange(@PathVariable("id") Long id);

    @PostMapping("/csp/contract/save/unicom")
    @ApiOperation(value = "联通合同新增")
    void saveUnicom(@RequestBody @Valid ContractUTAdd req);

    @PostMapping("/csp/contract/upgrade/unicom")
    @ApiOperation(value = "联通合同变更")
    void upgradeUnicom(@RequestBody @Valid ContractUTUpgrade req);


    @GetMapping("/csp/contract/getInfo/unTel/{id}")
    @ApiOperation(value = "电信联通合同具体信息")
    ContractUTInfo getUnTelContract(@PathVariable("id") Long id);
    @GetMapping("/csp/contract/getInfo/unTelChange/{id}")
    @ApiOperation(value = "电信联通同变更具体信息")
    ContractUTInfo getUnTelContractChange(@PathVariable("id") Long id);

    @PostMapping("/csp/contract/save/telecom")
    @ApiOperation(value = "电信合同新增")
    void saveTelecom(@RequestBody @Valid ContractUTAdd req);

    @PostMapping("/csp/contract/upgrade/telecom")
    @ApiOperation(value = "电信合同变更")
    void upgradeTelecom(@RequestBody @Valid ContractUTUpgrade req);

    // 新增代理商合同
    @PostMapping("/csp/contract/supplier/save")
    @ApiOperation(value = "代理商合同新增")
    void saveSupplierContract(@RequestBody @Valid ContractSupplierAdd req);

    @PostMapping("/csp/contract/supplier/update")
    @ApiOperation(value = "代理商合同编辑")
    void updateSupplierContract(@RequestBody @Valid ContractSupplierUpdate req);

    @GetMapping("/csp/contract/supplier/{id}")
    @ApiOperation(value = "代理商合同具体信息")
    ContractSupplierInfo getSupplierContractInfo(@PathVariable("id") Long id);

    @PostMapping("/csp/contract/supplier/{id}/delete")
    @ApiOperation(value = "代理商合同删除")
    void deleteSupplierContract(@PathVariable("id") Long id);

    @PostMapping("/csp/contract/supplier/{id}/logOff")
    @ApiOperation(value = "代理商合同注销删除")
    void logOffSupplierContract(@PathVariable("id") Long id);

    @GetMapping("/contract/status/options")
    @ApiOperation(value = "查询合同状态配置")
    ContractStatusResp getContractStatusOptions();

    @PostMapping("/contract/new/permission")
    @ApiOperation(value = "新增合同权限")
    Boolean getNewContractPermission(@RequestBody @Valid ContractNewPermissionReq req);

    @PostMapping("/contract/checkForCanCreate")
    @ApiOperation(value = "新增合同校验")
    Boolean checkForCanCreate(@RequestBody @Valid ContractNewPermissionReq req);

}
