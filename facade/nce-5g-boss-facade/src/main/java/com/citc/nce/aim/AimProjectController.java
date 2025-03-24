package com.citc.nce.aim;

import com.citc.nce.aim.vo.AimProjectEditReq;
import com.citc.nce.aim.vo.AimProjectQueryListReq;
import com.citc.nce.aim.vo.AimProjectQueryReq;
import com.citc.nce.aim.vo.AimProjectResp;
import com.citc.nce.aim.vo.AimProjectSaveReq;
import com.citc.nce.aim.vo.AimProjectUpdateStatusReq;
import com.citc.nce.aim.vo.req.AimProjectTestReq;
import com.citc.nce.aim.vo.resp.AimProjectTestResp;
import com.citc.nce.annotation.BossAuth;
import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/9 17:50
 */
@BossAuth("/hang-short-aim/hangup-project")
@RestController
@Slf4j
@Api(value = "aim", tags = "挂短-项目")
public class AimProjectController {

    @Resource
    private AimProjectApi aimProjectApi;

    @ApiOperation(value = "新增项目", notes = "新增项目")
    @PostMapping("/aim/project/save")
    public int save(@RequestBody @Valid AimProjectSaveReq req) {
        return aimProjectApi.save(req);
    }

    @ApiOperation(value = "编辑项目", notes = "编辑项目")
    @PostMapping("/aim/project/edit")
    public int edit(@RequestBody @Valid AimProjectEditReq req){
        return aimProjectApi.edit(req);
    }

    @ApiOperation(value = "更新项目状态", notes = "更新项目状态")
    @PostMapping("/aim/project/updateStatus")
    public int updateStatus(@RequestBody @Valid AimProjectUpdateStatusReq req) {
        return aimProjectApi.updateStatus(req);
    }

    @ApiOperation(value = "查询项目", notes = "查询项目")
    @PostMapping("/aim/project/queryProject")
    public AimProjectResp queryProject(@RequestBody AimProjectQueryReq req) {
        return aimProjectApi.queryProject(req);
    }

    @ApiOperation(value = "查询项目列表", notes = "查询项目列表")
    @PostMapping("/aim/project/queryProjectList")
    public PageResult<AimProjectResp> queryProjectList(@RequestBody AimProjectQueryListReq req){
        return aimProjectApi.queryProjectList(req);
    }

    @PostMapping("/aim/aimProjectTest")
    @ApiOperation(value = "挂短项目测试")
    public AimProjectTestResp aimProjectTest(@RequestBody @Valid AimProjectTestReq req) {
        return aimProjectApi.aimProjectTest(req);
    }
}
