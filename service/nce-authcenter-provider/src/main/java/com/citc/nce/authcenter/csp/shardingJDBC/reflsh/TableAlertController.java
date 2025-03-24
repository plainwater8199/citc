package com.citc.nce.authcenter.csp.shardingJDBC.reflsh;

import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bydud
 * @since 2024/3/20
 */
@RestController
@RequestMapping("alertTable")
public class TableAlertController {

    @Autowired
    private CspCustomerMapper customerMapper;

    //密码Bc列
    @GetMapping("addOutOfTime")
    public void addColumOutOfTime() {
        customerMapper.addColumOutOfTime();
    }

}
