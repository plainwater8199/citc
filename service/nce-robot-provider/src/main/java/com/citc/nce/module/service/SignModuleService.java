package com.citc.nce.module.service;

import com.citc.nce.module.entity.SignModuleDo;
import com.citc.nce.module.vo.req.SignModuleReq;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.req.SignModuleUpdateReq;

import java.util.List;

public interface SignModuleService {

    int saveSignModule(SignModuleReq req);

    int deleteSignModule(SignModuleReq req);

    int updateSignModule(SignModuleUpdateReq req);

    PageResult<SignModuleReq> getSignModuleList(SignModuleReq req);

    List<SignModuleReq> getSignModules();

    SignModuleReq getSignModule(SignModuleReq req);

    SignModuleDo getSignModuleById(String signModuleId);
}
