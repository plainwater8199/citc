package com.citc.nce.authcenter.sysmsg.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.authcenter.identification.vo.resp.SendSystemMessageResp;
import com.citc.nce.authcenter.systemmsg.vo.SysMsgManageListInfo;
import com.citc.nce.authcenter.systemmsg.vo.req.*;
import com.citc.nce.authcenter.systemmsg.vo.resp.AddSysMsgManageResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.ImportUserByCSVResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.QuerySysMsgManageDetailResp;
import com.citc.nce.authcenter.systemmsg.vo.resp.UpdateSysMsgManageResp;
import org.springframework.web.multipart.MultipartFile;

public interface AdminSysMsgManageService {
    AddSysMsgManageResp addSysMsgManage(AddSysMsgManageReq req);

    UpdateSysMsgManageResp updateSysMsgManage(UpdateSysMsgManageReq req);

    PageResult<SysMsgManageListInfo> querySysMsgManageList(QuerySysMsgManageListReq req);

    QuerySysMsgManageDetailResp querySysMsgManageDetail(QuerySysMsgManageDetailReq req);

    SendSystemMessageResp sendSysMsg(SendSystemMessageReq req);

    ImportUserByCSVResp importUserByCSV(MultipartFile file);

    void deleteSysMsgManage(DeleteSysMsgManageReq req);

    void sendTimeSysMsg();
}
