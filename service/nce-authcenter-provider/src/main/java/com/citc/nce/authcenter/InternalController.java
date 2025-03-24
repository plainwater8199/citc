package com.citc.nce.authcenter;

import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerMapper;
import com.citc.nce.authcenter.csp.multitenant.service.CspCreateTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 内部调用controller 用于数据变更脚本执行
 *
 * @author jcrenc
 * @since 2024/2/28 9:32
 */
@RequiredArgsConstructor
@RestController
public class InternalController {
    private final CspCustomerMapper cspCustomerMapper;
    @Resource
    private CspCreateTableService cspCreateTableService;

    @RequestMapping("test/addPayTypeColum")
    public void addPayTypeColum() {
        try {
            cspCustomerMapper.deletePayTypeColum();
        } catch (Exception ignore) {
        }

        cspCustomerMapper.addPayTypeColum();
    }

    @RequestMapping("test/addEnableAgentLogin")
    public void addEnableAgentLogin() {
        try {
            cspCustomerMapper.deleteEnableAgentLogin();
        } catch (Exception ignore) {
        }

        cspCustomerMapper.addEnableAgentLogin();
    }

    @RequestMapping("test/addBalanceField")
    public void addBalanceField() {
        cspCustomerMapper.addBalanceField();
    }

    @GetMapping("test/initChargeConsumeRecordTable")
    public void refreshChargeConsumeRecordTable() {
        cspCreateTableService.refreshChargeConsumeRecordTable();
    }
}
