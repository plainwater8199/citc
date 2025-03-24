package com.citc.nce.csp;

import com.citc.nce.auth.csp.contract.ContractApi;
import com.citc.nce.auth.csp.contract.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "ContractController", tags = "CSP--合同管理")
public class ContractController {

    @Resource
    private ContractApi contractApi;
    @Resource
    private ECDHService ecdhService;

    @PostMapping("/contract/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @HasCsp
    public PageResult<ContractResp> queryList(@RequestBody ContractReq req) {
        return contractApi.queryList(req);
    }

    @PostMapping("/contract/setServiceCode")
    @ApiOperation(value = "分配服务代码", notes = "分配服务代码")
    @HasCsp
    @Log(title = "分配服务代码")
    public int setServiceCode(@RequestBody ContractServiceCodeReq req) {
        return contractApi.setServiceCode(req);
    }

    @PostMapping("/contract/cleanServiceCode")
    @ApiOperation(value = "回收服务代码", notes = "回收服务代码")
    @HasCsp
    @Log(title = "回收服务代码")
    public int cleanServiceCode(@RequestBody ContractServiceCodeReq req) {
        return contractApi.cleanServiceCode(req);
    }

    @PostMapping("/contract/save/cmcc")
    @ApiOperation(value = "移动合同新增")
    @HasCsp
    @Log(title = "移动合同新增")
    public void saveCmcc(@RequestBody @Valid ContractCmccAdd req) {
        contractApi.saveCmcc(req);
    }

    @PostMapping("/contract/upgrade/cmcc")
    @HasCsp
    @ApiOperation(value = "移动合同变更")
    @Log(title = "移动合同变更")
    public void upgradeCmcc(@RequestBody @Valid ContractCmccUpgrade req) {
        contractApi.upgradeCmcc(req);
    }

    @GetMapping("/contract/getInfo/cmcc/{id}")
    @HasCsp
    @ApiOperation(value = "移动合同具体信息")
    public ContractCmccInfo getCmccContract(@PathVariable("id") Long id) {
        ContractCmccInfo body = contractApi.getCmccContract(id);
        if (StringUtils.hasLength(body.getContactPhoneNumber())) {
            body.setContactPhoneNumber(ecdhService.encode(body.getContactPhoneNumber()));
        }
        if (StringUtils.hasLength(body.getContractLegalPersonCardNumber())) {
            body.setContractLegalPersonCardNumber(ecdhService.encode(body.getContractLegalPersonCardNumber()));
        }
        return body;
    }

    @GetMapping("/contract/getInfo/cmccChange/{id}")
    @HasCsp
    @ApiOperation(value = "移动合同变更具体信息")
    public ContractCmccInfo getCmccContractChange(@PathVariable("id") Long id) {
        ContractCmccInfo body = contractApi.getCmccContractChange(id);
        if (StringUtils.hasLength(body.getContactPhoneNumber())) {
            body.setContactPhoneNumber(ecdhService.encode(body.getContactPhoneNumber()));
        }
        if (StringUtils.hasLength(body.getContractLegalPersonCardNumber())) {
            body.setContractLegalPersonCardNumber(ecdhService.encode(body.getContractLegalPersonCardNumber()));
        }
        return body;
    }

    @PostMapping("/contract/save/unicom")
    @HasCsp
    @ApiOperation(value = "联通合同新增")
    @Log(title = "联通合同新增")
    public void saveUnicom(@RequestBody @Valid ContractUTAdd req) {
        contractApi.saveUnicom(req);
    }


    @PostMapping("/contract/upgrade/unicom")
    @HasCsp
    @ApiOperation(value = "联通合同变更")
    @Log(title = "联通合同变更")
    public void upgradeUnicom(@RequestBody @Valid ContractUTUpgrade req) {
        contractApi.upgradeUnicom(req);
    }

    @PostMapping("/contract/save/telecom")
    @HasCsp
    @ApiOperation(value = "电信合同新增")
    @Log(title = "电信合同新增")
    public void saveTelecom(@RequestBody @Valid ContractUTAdd req) {
        contractApi.saveTelecom(req);
    }

    @PostMapping("/contract/upgrade/telecom")
    @HasCsp
    @ApiOperation(value = "电信合同变更")
    @Log(title = "电信合同变更")
    public void upgradeTelecom(@RequestBody @Valid ContractUTUpgrade req) {
        contractApi.upgradeTelecom(req);
    }

    @GetMapping("/contract/getInfo/unTel/{id}")
    @HasCsp
    @ApiOperation(value = "电信联通合同具体信息")
    public ContractUTInfo getUnTelContract(@PathVariable("id") Long id) {
        ContractUTInfo body = contractApi.getUnTelContract(id);
        if (StringUtils.hasLength(body.getWorkPhone())) {
            body.setWorkPhone(ecdhService.encode(body.getWorkPhone()));
        }
        if (StringUtils.hasLength(body.getContractLegalPersonCardNumber())) {
            body.setContractLegalPersonCardNumber(ecdhService.encode(body.getContractLegalPersonCardNumber()));
        }
        if (StringUtils.hasLength(body.getContactPhoneNumber())) {
            body.setContactPhoneNumber(ecdhService.encode(body.getContactPhoneNumber()));
        }
        return body;
    }

    @GetMapping("contract/getInfo/unTelChange/{id}")
    @HasCsp
    @ApiOperation(value = "电信联通同变更具体信息")
    public ContractUTInfo getUnTelContractChange(@PathVariable("id") Long id) {
        ContractUTInfo body = contractApi.getUnTelContractChange(id);
        if (StringUtils.hasLength(body.getWorkPhone())) {
            body.setWorkPhone(ecdhService.encode(body.getWorkPhone()));
        }
        if (StringUtils.hasLength(body.getContractLegalPersonCardNumber())) {
            body.setContractLegalPersonCardNumber(ecdhService.encode(body.getContractLegalPersonCardNumber()));
        }
        if (StringUtils.hasLength(body.getContactPhoneNumber())) {
            body.setContactPhoneNumber(ecdhService.encode(body.getContactPhoneNumber()));
        }
        return body;
    }

    // 新增代理商合同
    @PostMapping("/contract/supplier/save")
    @HasCsp
    @ApiOperation(value = "代理商合同新增")
    public void saveSupplierContract(@RequestBody @Valid ContractSupplierAdd req) {
        contractApi.saveSupplierContract(req);
    }

    @PostMapping("/contract/supplier/update")
    @HasCsp
    @ApiOperation(value = "代理商合同编辑")
    public void updateSupplierContract(@RequestBody @Valid ContractSupplierUpdate req) {
        contractApi.updateSupplierContract(req);
    }

    @GetMapping("/contract/supplier/{id}")
    @HasCsp
    @ApiOperation(value = "代理商合同具体信息")
    public ContractSupplierInfo getSupplierContractInfo(@PathVariable("id") Long id) {
        ContractSupplierInfo info = contractApi.getSupplierContractInfo(id);
        info.setWorkPhone(ecdhService.encode(info.getWorkPhone()));
        info.setContactPhoneNumber(ecdhService.encode(info.getContactPhoneNumber()));
        return info;
    }



    @PostMapping("/contract/supplier/{id}/delete")
    @HasCsp
    @ApiOperation(value = "代理商合同删除")
    public void deleteSupplierContract(@PathVariable("id") Long id) {
        contractApi.deleteSupplierContract(id);
    }



    @PostMapping("/contract/supplier/{id}/logOff")
    @HasCsp
    @ApiOperation(value = "代理商合同注销")
    public void logOffSupplierContract(@PathVariable("id") Long id) {
        contractApi.logOffSupplierContract(id);
    }



    @GetMapping("/contract/status/options")
    @HasCsp
    @ApiOperation(value = "查询合同状态配置")
    public ContractStatusResp getContractStatusOptions() {
        return contractApi.getContractStatusOptions();
    }



    @PostMapping("/contract/new/permission")
    @HasCsp
    @ApiOperation(value = "新增合同权限")
    public Boolean getNewContractPermission(@RequestBody @Valid ContractNewPermissionReq req) {
        return contractApi.getNewContractPermission(req);
    }

    @PostMapping("/contract/checkForCanCreate")
    @HasCsp
    @ApiOperation(value = "新增合同校验")
    public Boolean checkForCanCreate(@RequestBody @Valid ContractNewPermissionReq req) {
        return contractApi.checkForCanCreate(req);
    }
}
