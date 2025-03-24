package com.citc.nce.aim.privatenumber;

import com.citc.nce.aim.privatenumber.vo.*;
import com.citc.nce.aim.privatenumber.vo.req.PrivateNumberProjectTestReq;
import com.citc.nce.aim.privatenumber.vo.resp.PrivateNumberProjectTestResp;
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
@BossAuth("/hang-short-aim/privacy-project")
@RestController
@Slf4j
@Api(value = "aim", tags = "挂短-隐私号项目")
public class PrivateNumberProjectController {

    @Resource
    private PrivateNumberProjectApi privateNumberProjectApi;

    @ApiOperation(value = "新增项目", notes = "新增项目")
    @PostMapping("/privateNumber/project/save")
    public int save(@RequestBody @Valid PrivateNumberProjectSaveReq req) {
        return privateNumberProjectApi.save(req);
    }

    @ApiOperation(value = "编辑项目", notes = "编辑项目")
    @PostMapping("/privateNumber/project/edit")
    public int edit(@RequestBody @Valid PrivateNumberProjectEditReq req){
        return privateNumberProjectApi.edit(req);
    }

    @ApiOperation(value = "更新项目状态", notes = "更新项目状态")
    @PostMapping("/privateNumber/project/updateStatus")
    public int updateStatus(@RequestBody @Valid PrivateNumberProjectUpdateStatusReq req) {
        return privateNumberProjectApi.updateStatus(req);
    }

    @ApiOperation(value = "查询项目", notes = "查询项目")
    @PostMapping("/privateNumber/project/queryProject")
    public PrivateNumberProjectResp queryProject(@RequestBody PrivateNumberProjectQueryReq req) {
        return privateNumberProjectApi.queryProject(req);
    }

    @ApiOperation(value = "查询项目列表", notes = "查询项目列表")
    @PostMapping("/privateNumber/project/queryProjectList")
    public PageResult<PrivateNumberProjectResp> queryProjectList(@RequestBody PrivateNumberProjectQueryListReq req){
        return privateNumberProjectApi.queryProjectList(req);
    }

    @PostMapping("/privateNumber/aimProjectTest")
    @ApiOperation(value = "挂短项目测试")
    public PrivateNumberProjectTestResp aimProjectTest(@RequestBody @Valid PrivateNumberProjectTestReq req) {
        return privateNumberProjectApi.privateNumberProjectTest(req);
    }
}
