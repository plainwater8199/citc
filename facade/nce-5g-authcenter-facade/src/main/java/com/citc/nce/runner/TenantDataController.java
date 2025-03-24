package com.citc.nce.runner;

import com.citc.nce.authcenter.identification.vo.resp.GetPersonIdentificationResp;
import com.citc.nce.authcenter.legalaffairs.LegalAffairsApi;
import com.citc.nce.authcenter.legalaffairs.vo.req.IdReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.PageReq;
import com.citc.nce.authcenter.legalaffairs.vo.req.UpdateReq;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileNewestResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalFileResp;
import com.citc.nce.authcenter.legalaffairs.vo.resp.LegalRecordResp;
import com.citc.nce.authcenter.userDataSyn.TenantDataSynApi;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@Component
@RestController
public class TenantDataController {

    @Resource
    private TenantDataSynApi tenantDataSynApi;

    @ApiOperation("用户端-查询登录人个人认证信息")
    @GetMapping("/captcha/tenantData")
    public void tenantData() {
        System.out.println("----------------开始同步数据----------------");
        tenantDataSynApi.dataSyn();
    }


}