package com.citc.nce.im.mall.process;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.mall.process.service.MallRobotProcessService;
import com.citc.nce.robot.api.mall.process.MallRobotProcessApi;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDeleteReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessQueryListReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessUpdateReq;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessResp;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessTriggerDetailResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 流程管理
 * @Author: yangchuang
 * @Date: 2022/7/8 11:11
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class MallRobotProcessController implements MallRobotProcessApi {

    @Resource
    MallRobotProcessService service;


    @Override
    public PageResult<MallRobotProcessResp> queryList(MallRobotProcessQueryListReq req) {
        return service.queryList(req.getPageNo(), req.getPageSize(), null, req.getTemplateId());
    }

    @Override
    public int save(MallRobotProcessSaveReq req) {
        return service.save(req);
    }

    @Override
    public int update(MallRobotProcessUpdateReq req) {
        return service.update(req);
    }

    @Override
    public int delete(MallRobotProcessDeleteReq req) {
        return service.delete(req.getProcessId());
    }

    @Override
    public MallRobotProcessResp queryDetail(MallRobotProcessDetailReq req) {
        return service.queryDetail(req.getProcessId());
    }

    @Override
    public int addTrigger(MallRobotProcessTriggerSaveReq req) {
        return service.addTrigger(req);
    }

    @Override
    public MallRobotProcessTriggerDetailResp queryTriggerDetail(MallRobotProcessTriggerDetailReq req) {
        return service.queryTriggerDetail(req.getProcessId(), req.getTemplateId());
    }
}
