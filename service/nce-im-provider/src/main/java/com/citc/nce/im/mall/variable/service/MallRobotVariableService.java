package com.citc.nce.im.mall.variable.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableSaveReq;
import com.citc.nce.robot.api.mall.variable.vo.req.MallRobotVariableUpdateReq;
import com.citc.nce.robot.api.mall.variable.vo.resp.MallRobotVariableDetailResp;

import java.util.List;

public interface MallRobotVariableService {


    int save(MallRobotVariableSaveReq req);
    int update(MallRobotVariableUpdateReq req);
    int delete(Long id);
    MallRobotVariableDetailResp queryDetail(Long id);
    PageResult<MallRobotVariableDetailResp> queryList(Integer pageNo, Integer pageSize, String userId);

    List<MallRobotVariableDetailResp> listByIdsDel(List<String> ids);
    List<MallRobotVariableDetailResp> listByIds(List<String> ids);
}
