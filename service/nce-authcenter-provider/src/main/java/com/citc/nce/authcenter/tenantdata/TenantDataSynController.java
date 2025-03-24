package com.citc.nce.authcenter.tenantdata;

import com.citc.nce.authcenter.tenantdata.service.TenantDataSynService;
import com.citc.nce.authcenter.tenantdata.vo.DataSynReq;
import com.citc.nce.authcenter.userDataSyn.TenantDataSynApi;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class TenantDataSynController implements TenantDataSynApi {


    @Resource
    private TenantDataSynService tenantDataSynService;


    @ApiOperation("事务测试")
    @PostMapping("/tenant/dropTable")
    public void dropTable(@RequestBody DataSynReq req)  {
        tenantDataSynService.dropTable(req);
    }


    @Override
    public void dataSyn() {
        DataSynReq req = new DataSynReq();
        tenantDataSynService.dataSyn(req);
    }

}
