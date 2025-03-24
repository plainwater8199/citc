package com.citc.nce.adminPlatform;

import com.citc.nce.annotation.BossAuth;
import com.citc.nce.authcenter.identification.IdentificationApi;
import com.citc.nce.authcenter.identification.vo.IdentificationAuditItem;
import com.citc.nce.authcenter.identification.vo.UserCertificateItem;
import com.citc.nce.authcenter.identification.vo.UserTagLogItem;
import com.citc.nce.authcenter.identification.vo.req.*;
import com.citc.nce.authcenter.identification.vo.resp.AddUserCertificateResp;
import com.citc.nce.authcenter.identification.vo.resp.DashboardUserStatisticsResp;
import com.citc.nce.authcenter.identification.vo.resp.GetCertificateListResp;
import com.citc.nce.authcenter.identification.vo.resp.GetClientUserIdentificationResp;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bydud
 * @since 2024/3/13
 */

@Api(tags = "认证管理")
@RestController
public class IdentificationController {

    @Resource
    private IdentificationApi identificationApi;

    /**
     * 获取用户资质（标签）下拉列表
     */
    @ApiOperation("管理平台--获取用户资质（标签）下拉列表")
    @GetMapping("/admin/user/certificate/getCertificateList")
    public GetCertificateListResp getCertificateList() {
        return identificationApi.getCertificateList();
    }


    /**
     * 管理平台--开启关闭用户标签
     */
    @ApiOperation("管理平台--开启关闭用户标签")
    @PostMapping("/admin/user/onOrOffUserCertificate")
    public void onOrOffUserCertificate(@RequestBody OnOrOffUserCertificateReq req) {
        identificationApi.onOrOffUserCertificate(req);
    }


    /**
     * 管理平台--新增用户标签
     */
    @ApiOperation("管理平台--新增用户标签")
    @PostMapping("/admin/user/addUserCertificate")
    public AddUserCertificateResp addUserCertificate(@RequestBody AddUserCertificateReq req) {
        return identificationApi.addUserCertificate(req);
    }


    /**
     * 管理平台--更新用户标签状态
     */
    @ApiOperation("管理平台--更新用户标签状态")
    @PostMapping("/admin/user/updateUserCertificate")
    public void updateUserCertificate(@RequestBody UpdateUserCertificateReq req) {
        identificationApi.updateUserCertificate(req);
    }



    /**
     * 通过用户资质信息表id查询日志操作信息
     */
    @ApiOperation("管理平台--通过用户资质信息表id查询日志操作信息")
    @PostMapping("/admin/user/getUserTagLogByCertificateOptionsId")
    public List<UserTagLogItem> getUserTagLogByCertificateOptionsId(@RequestBody GetUserTagLogByCertificateOptionsIdReq req) {
        return identificationApi.getUserTagLogByCertificateOptionsId(req).getUserTagLogItems();
    }


    /**
     * 管理端 查看用户标签
     */
    @BossAuth("/unified-user-view/compile")
    @ApiOperation("管理平台--查看用户标签")
    @PostMapping("/admin/user/getClientUserCertificate")
    public List<UserCertificateItem> getClientUserCertificate(@RequestBody GetClientUserCertificateReq req) {
        return identificationApi.getClientUserCertificate(req).getUserCertificateItems();
    }

    /**
     * 管理端 查看审核备注历史
     */
    @BossAuth("/unified-user-view/details")
    @ApiOperation("管理平台--查看审核备注列表")
    @PostMapping("/admin/user/viewRemarkHistory")
    public List<IdentificationAuditItem> viewRemarkHistory(@RequestBody ViewRemarkHistoryReq req) {
        return identificationApi.viewRemarkHistory(req).getIdentificationAuditItems();
    }

    /**
     * 管理端--审核用户认证
     */
    @BossAuth("/unified-user-view/details")
    @ApiOperation("管理平台--审核用户认证")
    @PostMapping("/admin/user/auditIdentification")
    @Log(title = "管理端-审核用户认证", operatorType = OperatorType.MANAGE, isSaveRequestData = false,isSaveResponseData = false)
    public void auditIdentification(@RequestBody AuditIdentificationReq req) {
        identificationApi.auditIdentification(req);
    }

    /**
     * 管理端--获取客户端用户认证信息
     */
    @BossAuth({"/chatbot-view/audit","/unified-user-view/details","/chatbot-view/auditDetail"})
    @ApiOperation("管理平台--获取客户端用户认证信息")
    @PostMapping("/admin/user/getClientUserIdentifications")
    public GetClientUserIdentificationResp getClientUserIdentifications(@RequestBody GetClientUserIdentificationReq req) {
        return identificationApi.getClientUserIdentifications(req);
    }

    /**
     * 管理端dashboard用户统计
     */
    @ApiOperation("管理平台--用户标签统计")
    @PostMapping("/admin/user/getDashboardUserStatistics")
    public DashboardUserStatisticsResp getDashboardUserStatistics() {
        return identificationApi.getDashboardUserStatistics();
    }
}
