package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimProjectEditReq;
import com.citc.nce.aim.vo.AimProjectQueryListReq;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.aim.vo.AimProjectSaveReq;
import com.citc.nce.aim.vo.AimProjectUpdateStatusReq;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>挂短-项目</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:35
 */
@FeignClient(value = "rebot-service",contextId="AimProjectApi", url = "${robot:}")
public interface AimProjectApi {

    @PostMapping("/aim/project/save")
    int save(@RequestBody @Valid AimProjectSaveReq req);

    @PostMapping("/aim/project/edit")
    int edit(@RequestBody @Valid AimProjectEditReq req);

    @PostMapping("/aim/project/updateStatus")
    int updateStatus(@RequestBody @Valid AimProjectUpdateStatusReq req);

    @PostMapping("/aim/project/queryProject")
    AimProjectResp queryProject(@RequestBody AimProjectQueryReq req);

    @PostMapping("/aim/project/queryProjectList")
    PageResult<AimProjectResp> queryProjectList(@RequestBody AimProjectQueryListReq req);

    @ApiOperation("昨日流程趋势分页")
    @PostMapping("/aim/aimProjectTest")
    AimProjectTestResp aimProjectTest(@RequestBody @Valid AimProjectTestReq req);

}
