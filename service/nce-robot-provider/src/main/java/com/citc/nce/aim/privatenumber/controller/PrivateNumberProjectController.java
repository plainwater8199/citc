package com.citc.nce.aim.privatenumber.controller;

import com.citc.nce.aim.AimProjectApi;
import com.citc.nce.aim.privatenumber.PrivateNumberProjectApi;
import com.citc.nce.aim.privatenumber.service.PrivateNumberProjectService;
import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberProjectTestReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberProjectTestResp;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.vo.*;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>挂短-项目</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:59
 */
@RestController
public class PrivateNumberProjectController implements PrivateNumberProjectApi {

    @Resource
    PrivateNumberProjectService service;



    @Override
    public int save(PrivateNumberProjectSaveReq req) {
        return service.save(req);
    }

    @Override
    public int edit(PrivateNumberProjectEditReq req) {
        return service.edit(req);
    }

    @Override
    public int updateStatus(PrivateNumberProjectUpdateStatusReq req) {
        return service.updateStatus(req);
    }

    @Override
    public PrivateNumberProjectResp queryProject(PrivateNumberProjectQueryReq req) {
        return service.queryProject(req);
    }

    @Override
    public PageResult<PrivateNumberProjectResp> queryProjectList(PrivateNumberProjectQueryListReq req) {
        return service.queryProjectList(req);
    }

    @Override
    public PrivateNumberProjectTestResp privateNumberProjectTest(PrivateNumberProjectTestReq req) {
        return service.privateNumberProjectTest(req);
    }
}
