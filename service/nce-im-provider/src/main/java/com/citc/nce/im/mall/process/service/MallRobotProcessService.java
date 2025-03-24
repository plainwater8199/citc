package com.citc.nce.im.mall.process.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessUpdateReq;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessResp;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessTriggerDetailResp;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 11:14
 * @Version: 1.0
 * @Description:
 */
public interface MallRobotProcessService {
    
    int save(MallRobotProcessSaveReq req);

    PageResult<MallRobotProcessResp> queryList(Integer pageNo, Integer pageSize, String userId, String templateId);

    int update(MallRobotProcessUpdateReq req);

    int delete(String processId);

    MallRobotProcessResp queryDetail(String processId);

    int addTrigger(MallRobotProcessTriggerSaveReq req);

    MallRobotProcessTriggerDetailResp queryTriggerDetail(String processId, String templateId);
}
