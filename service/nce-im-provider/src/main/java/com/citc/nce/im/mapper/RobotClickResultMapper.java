package com.citc.nce.im.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.citc.nce.im.entity.RobotClickResult;
import com.citc.nce.robot.req.SendPageReq;
import com.citc.nce.robot.vo.AnalysisResp;

import java.util.Date;
import java.util.List;

public interface RobotClickResultMapper extends BaseMapper<RobotClickResult> {

    List<AnalysisResp> queryByDay(SendPageReq sendPageReq);

    List<AnalysisResp> queryByHour(SendPageReq sendPageReq);

    List<AnalysisResp> queryGroupByBtnUuid(SendPageReq sendPageReq);
}
