package com.citc.nce.authcenter.sysmsg;

import com.citc.nce.authcenter.identification.service.IdentificationService;
import com.citc.nce.authcenter.identification.vo.CertificateItem;
import com.citc.nce.authcenter.identification.vo.resp.GetCertificateListResp;
import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.sysmsg.service.AdminSysMsgManageService;
import com.citc.nce.authcenter.systemmsg.AdminSysMsgManageApi;
import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.AddSysMsgManageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.ImportUserByCSVResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.QuerySysMsgManageDetailResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UpdateSysMsgManageResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController()
@Slf4j
public class AdminSysMsgManageController implements AdminSysMsgManageApi {

    @Resource
    private AdminSysMsgManageService adminSysMsgManageService;
    @Resource
    private IdentificationService identificationService;

    @Override
    @ApiOperation("管理平台--新增站内信")
    @PostMapping("/admin/sysMsg/addSysMsgManage")
    public AddSysMsgManageResp addSysMsgManage(@RequestBody @Valid AddSysMsgManageReq req) {
        return adminSysMsgManageService.addSysMsgManage(req);
    }

    @Override
    @ApiOperation("管理平台--更改站内信")
    @PostMapping("/admin/sysMsg/updateSysMsgManage")
    public UpdateSysMsgManageResp updateSysMsgManage(@RequestBody @Valid UpdateSysMsgManageReq req) {
        return adminSysMsgManageService.updateSysMsgManage(req);
    }

    @Override
    @ApiOperation("管理平台--查询站内信列表")
    @PostMapping("/admin/sysMsg/querySysMsgManageList")
    public PageResult<SysMsgManageListInfo> querySysMsgManageList(@RequestBody QuerySysMsgManageListReq req) {
        return adminSysMsgManageService.querySysMsgManageList(req);
    }

    @Override
    @ApiOperation("管理平台--查询站内信详情")
    @PostMapping("/admin/sysMsg/querySysMsgManageDetail")
    public QuerySysMsgManageDetailResp querySysMsgManageDetail(@RequestBody @Valid QuerySysMsgManageDetailReq req) {
        return adminSysMsgManageService.querySysMsgManageDetail(req);
    }

    @Override
    @ApiOperation("管理平台--发送站内信")
    @PostMapping("/admin/sysMsg/sendSysMsg")
    public SendSystemMessageResp sendSysMsg(@RequestBody @Valid SendSystemMessageReq req) {
        return adminSysMsgManageService.sendSysMsg(req);
    }

    @Override
    @ApiOperation("管理平台--获取用户标签列表")
    @PostMapping("/admin/sysMsg/getCertificateList")
    public GetCertificateListResp getCertificateList(@RequestBody GetCertificateListReq req) {
        GetCertificateListResp resp = new GetCertificateListResp();
        List<CertificateItem> certificateItems = identificationService.getCertificateList(req.getPlats());
        resp.setCertificateItems(certificateItems);
        return resp;
    }

    @Override
    @ApiOperation("管理平台--公告csv批量导入用户")
    @PostMapping("/admin/sysMsg/importUserByCSV")
    public ImportUserByCSVResp importUserByCSV(@RequestPart(value = "file") MultipartFile file) {
        return adminSysMsgManageService.importUserByCSV(file);
    }

    @Override
    @ApiOperation("管理平台--删除站内信")
    @PostMapping("/deleteSysMsgManage")
    public void deleteSysMsgManage(DeleteSysMsgManageReq req) {
        adminSysMsgManageService.deleteSysMsgManage(req);
    }

    @Override
    @ApiOperation("管理平台--定时发送站内信")
    @PostMapping("/sendTimeSysMsg")
    public void sendTimeSysMsg() {
        adminSysMsgManageService.sendTimeSysMsg();
    }
}
