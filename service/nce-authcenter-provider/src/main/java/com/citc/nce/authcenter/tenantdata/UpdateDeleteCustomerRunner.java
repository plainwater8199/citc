package com.citc.nce.authcenter.tenantdata;

import com.citc.nce.authcenter.tenantdata.user.dao.CspCustomer1Dao;
import com.citc.nce.authcenter.tenantdata.user.entity.CspCustomerDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@Order(10)
public class UpdateDeleteCustomerRunner implements ApplicationRunner {


    @Resource
    private CspCustomer1Dao cspCustomer1Dao;

    @Override
    public void run(ApplicationArguments args) {
        try {
            List<CspCustomerDo> cspCustomerDos = cspCustomer1Dao.queryDeletedList();
            log.info("----------------用户被错误删除回退操作【开始】------------------:" + cspCustomerDos.size());
            if (!CollectionUtils.isEmpty(cspCustomerDos)) {
                for (CspCustomerDo cspCustomerDo : cspCustomerDos) {
                    log.info(cspCustomerDo.getId() + "----" + cspCustomerDo.getDeleted() + "----" + cspCustomerDo.getCustomerId() + "----" + cspCustomerDo.getCspId() + "----" + cspCustomerDo.getUpdater());
                    cspCustomer1Dao.updateByCustomerId(cspCustomerDo.getCustomerId());
                }
                log.info("----------------用户被错误删除回退操作【结束】------------------");
            }
        }catch (Exception exception)
        {
            log.error("用户被错误删除回退操作异常",exception);
        }
    }
}
