package com.citc.nce.aim.controller;

import com.citc.nce.aim.AimProjectApi;
import com.citc.nce.aim.service.AimProjectService;
import com.citc.nce.aim.vo.AimProjectEditReq;
import com.citc.nce.aim.vo.AimProjectQueryListReq;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.aim.vo.AimProjectSaveReq;
import com.citc.nce.aim.vo.AimProjectUpdateStatusReq;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>挂短-项目</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:59
 */
@RestController
public class AimProjectController implements AimProjectApi {

    @Resource
    AimProjectService service;

    @Override
    public int save(AimProjectSaveReq req) {
        return service.save(req);
    }

    @Override
    public int edit(AimProjectEditReq req) {
        return service.edit(req);
    }

    @Override
    public int updateStatus(AimProjectUpdateStatusReq req) {
        return service.updateStatus(req);
    }

    @Override
    public AimProjectResp queryProject(AimProjectQueryReq req) {
        return service.queryProject(req);
    }

    @Override
    public PageResult<AimProjectResp> queryProjectList(AimProjectQueryListReq req) {
        return service.queryProjectList(req);
    }

    @Override
    public AimProjectTestResp aimProjectTest(AimProjectTestReq req) {
        return service.aimProjectTest(req);
    }

}
