package com.citc.nce.identification;

import com.citc.nce.authcenter.identification.IdentificationApi;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.*;
import com.citc.nce.common.web.utils.dh.ECDHService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "认证管理")
@RestController
public class IdentificationController {

    @Resource
    private IdentificationApi identificationApi;
    @Resource
    private ECDHService ecdhService;

    /**
     * 查询登录人个人认证信息
     */
    @ApiOperation("用户端-查询登录人个人认证信息")
    @GetMapping("/user/identification/getPersonIdentification")
    public GetPersonIdentificationResp getPersonIdentification() {
        return identificationApi.getPersonIdentification();
    }

    /**
     * 查询个人认证信息
     */
    @ApiOperation("用户端-查询个人认证信息")
    @GetMapping("/user/identification/getPersonIdentificationByUserId/{userId}")
    public GetPersonIdentificationResp getPersonIdentificationByUserId(@PathVariable("userId") String userId) {
        return identificationApi.getPersonIdentificationByUserId(userId);
    }

    /**
     * 个人实名认证申请
     */
    @ApiOperation("用户端--个人实名认证申请")
    @PostMapping("/user/identification/personIdentificationApply")
    public void personIdentificationApply(@RequestBody PersonIdentificationApplyReq personIdentificationReq) {
        identificationApi.personIdentificationApply(personIdentificationReq);
    }


    /**
     * 查询登陆用户企业认证信息
     */
    @ApiOperation("用户端--查询登陆用户企业认证信息")
    @PostMapping("/user/identification/getEnterpriseIdentificationInfo")
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfo() {
        return identificationApi.getEnterpriseIdentificationInfo();
    }

    /**
     * 查询用户企业认证信息
     */
    @ApiOperation("用户端--查询用户企业认证信息")
    @GetMapping("/user/identification/getEnterpriseIdentificationInfoByUserId/{userId}")
    public GetEnterpriseIdentificationResp getEnterpriseIdentificationInfoByUserId(@PathVariable("userId") String userId) {
        GetEnterpriseIdentificationResp body = identificationApi.getEnterpriseIdentificationInfoByUserId(userId);
        body.setIdCard(ecdhService.encode(body.getIdCard()));
        body.setCreditCode(ecdhService.encode(body.getCreditCode()));
        return body;
    }

    /**
     * 企业认证实名申请
     */
    @ApiOperation("用户端--企业认证实名申请")
    @PostMapping("/user/identification/enterpriseIdentificationApply")
    public void enterpriseIdentificationApply(@RequestBody EnterpriseIdentificationApplyReq req) {
        identificationApi.enterpriseIdentificationApply(req);
    }

    /**
     * 对企业账户名做唯一性校验
     */
    @ApiOperation("用户端--对企业账户名做唯一性校验")
    @GetMapping("/user/identification/checkEnterpriseAccountNameUnique")
    public boolean checkEnterpriseAccountNameUnique(@RequestParam("enterpriseAccountName") String enterpriseAccountName) {
        return identificationApi.checkEnterpriseAccountNameUnique(enterpriseAccountName).getResult();
    }


    /**
     * 获取用户资质（标签）列表
     */
    @ApiOperation("用户端--获取用户资质列表")
    @PostMapping("/user/certificate/queryCertificateOptionList")
    public QueryCertificateOptionListResp queryCertificateOptionList(@RequestBody QueryCertificateOptionListReq req) {
        return identificationApi.queryCertificateOptionList(req);
    }

    /**
     * 获取用户资质（标签）列表 包括未拥有的资质 也返回
     *
     */
    @ApiOperation("用户端--获取用户资质（标签）列表 包括未拥有的资质")
    @PostMapping("/user/certificate/getCertificateOptionsAll")
    public QueryAllCertificateOptionsResp queryAllCertificateOptions(@RequestBody QueryAllCertificateOptionsReq req) {
        return identificationApi.queryAllCertificateOptions(req);
    }


    @ApiOperation("用户端--校验能力提供商、解决方案商权限状态")
    @PostMapping("/user/certificate/checkPermissionStatus")
    public CheckPermissionStatusResp checkPermissionStatus(@RequestBody CheckPermissionStatusReq req){
        return identificationApi.checkPermissionStatus(req);
    }





}
