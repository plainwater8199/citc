package com.citc.nce.robot.api.mall.snapshot;

import com.citc.nce.robot.api.mall.snapshot.req.MallRobotOrderSaveOrUpdateReq;
import com.citc.nce.robot.api.mall.snapshot.resp.MallRobotOrderSaveOrUpdateResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>扩展商城-模板</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 15:35
 */
@FeignClient(value = "im-service", contextId = "MallSnapshotApi", url = "${im:}")
public interface MallSnapshotApi {

    @PostMapping("/mall/snapshot/saveOrUpdate")
    MallRobotOrderSaveOrUpdateResp saveOrUpdate(@RequestBody @Valid MallRobotOrderSaveOrUpdateReq req);

}
