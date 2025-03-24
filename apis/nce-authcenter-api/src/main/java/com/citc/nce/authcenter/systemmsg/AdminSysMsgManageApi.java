package com.citc.nce.authcenter.systemmsg;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.authcenter.identification.vo.resp.GetCertificateListResp;
import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.AddSysMsgManageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.ImportUserByCSVResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.QuerySysMsgManageDetailResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UpdateSysMsgManageResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Api(tags = "管理平台--站内信管理模块")
@FeignClient(value = "authcenter-service", contextId = "system-message-manage", url = "${authCenter:}")
public interface AdminSysMsgManageApi {

    @ApiOperation("管理平台--新增站内信")
    @PostMapping("/admin/sysMsg/addSysMsgManage")
    AddSysMsgManageResp addSysMsgManage(@RequestBody @Valid AddSysMsgManageReq req);
    @ApiOperation("管理平台--更改站内信")
    @PostMapping("/admin/sysMsg/updateSysMsgManage")
    UpdateSysMsgManageResp updateSysMsgManage(@RequestBody @Valid UpdateSysMsgManageReq req);
    @ApiOperation("管理平台--查询站内信列表")
    @PostMapping("/admin/sysMsg/querySysMsgManageList")
    PageResult<SysMsgManageListInfo> querySysMsgManageList(@RequestBody QuerySysMsgManageListReq req);
    @ApiOperation("管理平台--查询站内信详情")
    @PostMapping("/admin/sysMsg/querySysMsgManageDetail")
    QuerySysMsgManageDetailResp querySysMsgManageDetail(@RequestBody @Valid QuerySysMsgManageDetailReq req);
    @ApiOperation("管理平台--发送站内信")
    @PostMapping("/admin/sysMsg/sendSysMsg")
    SendSystemMessageResp sendSysMsg(@RequestBody @Valid SendSystemMessageReq req);
    @ApiOperation("管理平台--获取用户标签列表")
    @PostMapping("/admin/sysMsg/getCertificateList")
    GetCertificateListResp getCertificateList(@RequestBody GetCertificateListReq req);
    @ApiOperation("管理平台--公告csv批量导入用户")
    @PostMapping(value = "/admin/sysMsg/importUserByCSV",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ImportUserByCSVResp importUserByCSV(@RequestPart(value = "file") MultipartFile file);
    @ApiOperation("管理平台--删除站内信")
    @PostMapping("/deleteSysMsgManage")
    void deleteSysMsgManage(@RequestBody @Valid DeleteSysMsgManageReq req);
    @ApiOperation("管理平台--定时发送站内信")
    @PostMapping("/sendTimeSysMsg")
    void sendTimeSysMsg();
}
