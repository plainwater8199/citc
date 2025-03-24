package com.citc.nce.auth.csp.contract;

import com.citc.nce.auth.csp.account.entity.AccountManageDo;
import com.citc.nce.auth.csp.contract.dao.ContractManageChangeDao;
import com.citc.nce.auth.csp.contract.entity.ContractManageChangeDo;
import com.citc.nce.auth.csp.contract.entity.ContractManageDo;
import com.citc.nce.auth.csp.contract.service.ContractManageService;
import com.citc.nce.auth.csp.contract.vo.*;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp
 * @Author: litao
 * @CreateTime: 2023-02-13  10:22
 * @Version: 1.0
 */
@RestController
@Slf4j
public class ContractManageController implements ContractApi {
    @Resource
    private ContractManageService contractManageService;
    @Autowired
    private CommonKeyPairConfig commonKeyPairConfig;
    @Autowired
    private ContractManageChangeDao contractManageChangeDao;


    @Override
    public PageResult<ContractResp> queryList(@RequestBody @Valid ContractReq req) {
        return contractManageService.queryList(req);
    }

    @Override
    public int setServiceCode(@RequestBody @Valid ContractServiceCodeReq req) {
        return contractManageService.setServiceCode(req);
    }

    @Override
    public int cleanServiceCode(@RequestBody @Valid ContractServiceCodeReq req) {
        return contractManageService.cleanServiceCode(req);
    }

    @Override
    public int saveOrUpdate(@RequestBody @Valid ContractSaveReq req) {
        return contractManageService.saveOrUpdate(req);
    }

    @Override
    public ContractDetailResp getDetailById(@RequestBody @Valid ContractDetailReq req) {
        return contractManageService.getDetailById(req);
    }


    @Override
    public void saveCmcc(@RequestBody @Valid ContractCmccAdd req) {
        contractManageService.addContractCmcc(req);
    }

    @Override
    public void upgradeCmcc(ContractCmccUpgrade req) {
        contractManageService.upgradeCmcc(req);
    }

    @Override
    @GetMapping("/csp/contract/getInfo/cmcc/{id}")
    public ContractCmccInfo getCmccContract(@PathVariable("id") Long id) {
        return contractManageService.getCmccContract(id);
    }

    @GetMapping("/csp/contract/getInfo/cmccChange/{id}")
    @Override
    public ContractCmccInfo getCmccContractChange(@PathVariable("id") Long id) {
        return contractManageService.getCmccContractChange(id);
    }


    @Override
    public void saveUnicom(ContractUTAdd req) {
        contractManageService.saveUniTel(req, 1);
    }

    @Override
    public void upgradeUnicom(ContractUTUpgrade req) {
        contractManageService.upgradeUniTel(req, 1);
    }

    @Override
    public void saveTelecom(ContractUTAdd req) {
        contractManageService.saveUniTel(req, 3);
    }

    @Override
    public void upgradeTelecom(ContractUTUpgrade req) {
        contractManageService.upgradeUniTel(req, 3);
    }

    @Override
    @GetMapping("/csp/contract/getInfo/unTel/{id}")
    public ContractUTInfo getUnTelContract(@PathVariable("id") Long id) {
        return contractManageService.getUnTelContract(id);
    }

    @GetMapping("/csp/contract/getInfo/unTelChange/{id}")
    @Override
    public ContractUTInfo getUnTelContractChange(@PathVariable("id") Long id) {
        return contractManageService.getUnTelContractChange(id);
    }

    @RequestMapping("/csp/contract/init")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("csp合同隐私信息加密，初始化历史数据")
    public void init() {
        List<ContractManageDo> contractManageDos = contractManageService.list();
        if (CollectionUtils.isEmpty(contractManageDos))
            return;
        for (ContractManageDo contract : contractManageDos) {
            if (StringUtils.isNotEmpty(contract.getContactPhoneNumber()) && contract.getContactPhoneNumber().length() < 50)
                contract.setContactPhoneNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), contract.getContactPhoneNumber()));
            if (StringUtils.isNotEmpty(contract.getContractLegalPersonCardNumber()) && contract.getContractLegalPersonCardNumber().length() < 50)
                contract.setContractLegalPersonCardNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), contract.getContractLegalPersonCardNumber()));
            if (StringUtils.isNotEmpty(contract.getWorkPhone()) && contract.getWorkPhone().length() < 50)
                contract.setWorkPhone(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), contract.getWorkPhone()));
        }
        contractManageService.updateBatchById(contractManageDos);

        List<ContractManageChangeDo> contractManageChangeDos = contractManageChangeDao.selectList();
        if (CollectionUtils.isEmpty(contractManageChangeDos))
            return;
        for (ContractManageChangeDo changeDo : contractManageChangeDos) {
            if (StringUtils.isNotEmpty(changeDo.getContactPhoneNumber()) && changeDo.getContactPhoneNumber().length() < 50)
                changeDo.setContactPhoneNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), changeDo.getContactPhoneNumber()));
            if (StringUtils.isNotEmpty(changeDo.getContractLegalPersonCardNumber()) && changeDo.getContractLegalPersonCardNumber().length() < 50)
                changeDo.setContractLegalPersonCardNumber(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), changeDo.getContractLegalPersonCardNumber()));
            if (StringUtils.isNotEmpty(changeDo.getWorkPhone()) && changeDo.getWorkPhone().length() < 50)
                changeDo.setWorkPhone(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), changeDo.getWorkPhone()));
        }
        contractManageChangeDao.updateBatch(contractManageChangeDos);
    }

    @Override
    public void saveSupplierContract(@RequestBody @Valid ContractSupplierAdd req) {
        contractManageService.saveSupplierContract(req);
    }


    @Override
    public void updateSupplierContract(@RequestBody @Valid ContractSupplierUpdate req) {
        contractManageService.updateSupplierContract(req);
    }


    @Override
    public ContractSupplierInfo getSupplierContractInfo(@PathVariable("id") Long id) {
        return contractManageService.getSupplierContractInfo(id);
    }


    @Override
    public void deleteSupplierContract(@PathVariable("id") Long id) {
        contractManageService.deleteSupplierContract(id);
    }


    @Override
    public void logOffSupplierContract(@PathVariable("id") Long id) {
        contractManageService.logOffSupplierContract(id);
    }


    @Override
    public ContractStatusResp getContractStatusOptions() {
        return contractManageService.getContractStatusOptions();
    }


    @Override
    public Boolean getNewContractPermission(ContractNewPermissionReq req) {
        return contractManageService.getNewContractPermission(req);
    }

    @Override
    public Boolean checkForCanCreate(@RequestBody @Valid ContractNewPermissionReq req) {
        return contractManageService.checkForCanCreate(req);
    }
}
