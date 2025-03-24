package com.citc.nce.robot.api.mall.process;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDeleteReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessQueryListReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerDetailReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessTriggerSaveReq;
import com.citc.nce.robot.api.mall.process.vo.req.MallRobotProcessUpdateReq;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessResp;
import com.citc.nce.robot.api.mall.process.vo.resp.MallRobotProcessTriggerDetailResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(value = "im-service",contextId="MallRobotProcessApi", url = "${im:}")
public interface MallRobotProcessApi {
    /**
     * 流程管理列表获取
     *
     * @param
     * @return
     */
    @PostMapping("/mall/robotProcess/queryList")
    PageResult<MallRobotProcessResp> queryList(@RequestBody @Valid MallRobotProcessQueryListReq req);

    /**
     * 新增流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/mall/robotProcess/save")
    int save(@RequestBody @Valid MallRobotProcessSaveReq req);

    /**
     * 修改流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/mall/robotProcess/update")
    int update(@RequestBody @Valid MallRobotProcessUpdateReq req);

    /**
     * 删除流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/mall/robotProcess/delete")
    int delete(@RequestBody @Valid MallRobotProcessDeleteReq req);
    /**
     * 获取单个流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/mall/robotProcess/queryDetail")
    MallRobotProcessResp queryDetail(@RequestBody @Valid MallRobotProcessDetailReq req);

    @PostMapping("/mall/robotProcess/addTrigger")
    int addTrigger(@RequestBody @Valid MallRobotProcessTriggerSaveReq req);
    @PostMapping("/mall/robotProcess/queryTriggerDetail")
    MallRobotProcessTriggerDetailResp queryTriggerDetail(@RequestBody @Valid MallRobotProcessTriggerDetailReq req);
}
