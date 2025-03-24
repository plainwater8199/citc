package com.citc.nce.modulemanagement.service;

import com.citc.nce.modulemanagement.vo.ModuleManagementItem;
import com.citc.nce.modulemanagement.vo.ModuleManagementResp;
import com.citc.nce.modulemanagement.vo.req.QueryForMSReq;

import java.util.List;

public interface ModuleManagementService {
    ModuleManagementResp moduleQuery();

    ModuleManagementItem queryById(Long id);

    void updateMssID(Long id, Long mssId);

    ModuleManagementResp queryForMS(QueryForMSReq req);

    void deleteMssIDForIds(List<String> ids);
}
