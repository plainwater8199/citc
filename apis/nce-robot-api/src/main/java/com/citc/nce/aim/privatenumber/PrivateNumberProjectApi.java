package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberProjectTestReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberProjectTestResp;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * <p>挂短-隐私号项目</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:35
 */
@FeignClient(value = "rebot-service",contextId="PrivateNumberProjectApi", url = "${robot:}")
public interface PrivateNumberProjectApi {

    @PostMapping("/privateNumber/project/save")
    int save(@RequestBody @Valid PrivateNumberProjectSaveReq req);

    @PostMapping("/privateNumber/project/edit")
    int edit(@RequestBody @Valid PrivateNumberProjectEditReq req);

    @PostMapping("/privateNumber/project/updateStatus")
    int updateStatus(@RequestBody @Valid PrivateNumberProjectUpdateStatusReq req);

    @PostMapping("/privateNumber/project/queryProject")
    PrivateNumberProjectResp queryProject(@RequestBody PrivateNumberProjectQueryReq req);

    @PostMapping("/privateNumber/project/queryProjectList")
    PageResult<PrivateNumberProjectResp> queryProjectList(@RequestBody PrivateNumberProjectQueryListReq req);

    @ApiOperation("昨日流程趋势分页")
    @PostMapping("/privateNumber/aimProjectTest")
    PrivateNumberProjectTestResp privateNumberProjectTest(@RequestBody @Valid PrivateNumberProjectTestReq req);

}
