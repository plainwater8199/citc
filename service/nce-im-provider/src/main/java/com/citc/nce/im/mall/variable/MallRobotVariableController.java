package com.citc.nce.im.mall.variable;


import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.mall.variable.service.MallRobotVariableService;
import com.citc.nce.robot.api.mall.variable.MallRobotVariableApi;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDeleteReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableDetailReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableQueryListReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableSaveReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableUpdateReq;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
public class MallRobotVariableController implements MallRobotVariableApi {


    @Resource
    MallRobotVariableService service;

    @Override
    public int save(MallRobotVariableSaveReq req) {
        return service.save(req);
    }

    @Override
    public int update(MallRobotVariableUpdateReq req) {
        return service.update(req);
    }

    @Override
    public int delete(MallRobotVariableDeleteReq req) {
        return service.delete(req.getId());
    }

    @Override
    public MallRobotVariableDetailResp queryDetail(MallRobotVariableDetailReq req) {
        return service.queryDetail(req.getId());
    }

    @Override
    public PageResult<MallRobotVariableDetailResp> queryList(MallRobotVariableQueryListReq req) {
        return service.queryList(req.getPageNo(), req.getPageSize(), null);
    }

    @Override
    public List<MallRobotVariableDetailResp> listByIdsDel(List<String> ids) {
        return service.listByIdsDel(ids);
    }

}
