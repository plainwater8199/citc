package com.citc.nce.robot.controller;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.*;
import com.citc.nce.robot.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 场景管理
 *
 * @Author: yangchuang
 * @Date: 2022/7/7 17:18
 * @Version: 1.0
 * @Description:
 */

@RestController
@RequestMapping("/robot")
@Slf4j
@Api(value = "robot", tags = "场景管理，流程管理，机器人设置，触发器功能")
public class RobotSceneNodeController {

    @Resource
    private RobotSceneNodeApi robotSceneNodeApi;

    @Resource
    RobotSceneMaterialApi robotSceneMaterialApi;

    /**
     * 场景管理列表获取分页
     *
     * @param
     * @return
     */
    @ApiOperation(value = "场景管理列表获取分页", notes = "场景管理列表获取分页")
    @PostMapping("/scene/pageList")
    public PageResultResp getRobotSceneNodesPage(@RequestBody @Valid PageParam pageParam) {
        return robotSceneNodeApi.getRobotSceneNodes(pageParam);
    }


    @PostMapping("/scene/queryByName")
    @ApiOperation(value = "通过场景名获取", notes = "通过场景名获取")
    public RobotSceneNodeResp queryByName(@RequestBody RobotSceneNodeReq req) {
        return robotSceneNodeApi.queryByName(req);
    }

    /**
     * 场景管理列表获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "场景管理列表获取", notes = "场景管理列表获取")
    @GetMapping("/scene/list")
    public List<RobotSceneNodeResp> getRobotSceneNodeList() {
        return robotSceneNodeApi.getRobotSceneNodeList();
    }

    /**
     * 场景管理列表对应流程管理树获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "场景管理列表对应流程管理树获取", notes = "场景管理列表对应流程管理树获取")
    @GetMapping("/scene/listTree")
    public List<RobotSceneNodeTreeResp> getRobotSceneNodeListTree() {
        return robotSceneNodeApi.getRobotSceneNodeListTree();
    }

    /**
     * 新增场景管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增场景管理", notes = "新增场景管理")
    @PostMapping("/scene/save")
    public Long saveRobotSceneNode(@RequestBody @Valid RobotSceneNodeReq robotSceneNodeReq) {
        return robotSceneNodeApi.saveRobotSceneNode(robotSceneNodeReq);
    }

    /**
     * 修改场景管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改场景管理", notes = "修改场景管理")
    @PostMapping("/scene/edit")
    public int updateRobotSceneNode(@RequestBody @Valid RobotSceneNodeEditReq robotSceneNodeEditReq) {
        return robotSceneNodeApi.updateRobotSceneNode(robotSceneNodeEditReq);
    }

    /**
     * 删除场景管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除场景管理", notes = "删除场景管理")
    @PostMapping("/scene/delete")
    public int delRobotSceneNodeById(@RequestParam Long id) {
        return robotSceneNodeApi.delRobotSceneNodeById(id);
    }

    /**
     * 获取单个场景管理
     *
     * @param
     * @return
     */
    @PostMapping("/scene/getOne")
    public RobotSceneNodeResp getRobotSceneNodeById(@RequestBody Long id) {
        return robotSceneNodeApi.getRobotSceneNodeById(id);
    }

    /**
     * 校验账户是否关联场景
     *
     * @param
     * @return
     */
    @ApiOperation(value = "校验5G账户是否关联场景", notes = "校验5G账户是否关联场景")
    @PostMapping("/scene/checkAccountId")
    public boolean checkAccountId(@RequestBody String id) {
        return robotSceneNodeApi.checkAccountId(id);
    }

    @Resource
    private RobotProcessTriggerNodeApi robotProcessTriggerNodeApi;

    /**
     * 新增触发器
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增触发器", notes = "新增触发器")
    @PostMapping("/process/trigger/add")
    public int saveRobotProcessTriggerNodeReq(@RequestBody @Valid RobotProcessTriggerNodeReq robotProcessTriggerNodeReq) {
        return robotProcessTriggerNodeApi.saveRobotProcessTriggerNodeReq(robotProcessTriggerNodeReq);
    }

    /**
     * 获取场景下所有触发器
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取场景下所有触发器", notes = "获取场景下所有触发器")
    @PostMapping("/process/trigger/list")
    public List<RobotProcessTriggerNodesResp> getRobotProcessTriggerNodes(@RequestBody Long sceneId) {
        RobotProcessTriggerNodeReq robotProcessTriggerNodeReq = new RobotProcessTriggerNodeReq();
        robotProcessTriggerNodeReq.setSceneId(sceneId);
        return robotProcessTriggerNodeApi.getRobotProcessTriggerNodes(robotProcessTriggerNodeReq);
    }

    /**
     * 获取场景下单个触发器
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取场景下单个触发器", notes = "获取场景下单个触发器")
    @PostMapping("/process/trigger/getRobotProcessTriggerNode")
    public RobotProcessTriggerNodeResp getRobotProcessTriggerNode(@RequestBody @Valid RobotProcessTriggerNodeOneReq robotProcessTriggerNodeOneReq) {
        return robotProcessTriggerNodeApi.getRobotProcessTriggerNode(robotProcessTriggerNodeOneReq);
    }


    @Resource
    private RobotProcessSettingNodeApi robotProcessSettingNodeApi;

    /**
     * 流程管理列表获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "流程管理列表获取", notes = "流程管理列表获取")
    @PostMapping("/process/list")
    public PageResultResp getRobotProcessSettings(@RequestBody @Valid RobotProcessSettingNodePageReq robotProcessSettingNodePageReq) {
        return robotProcessSettingNodeApi.getRobotProcessSettings(robotProcessSettingNodePageReq);
    }

    /**
     * 流程管理列表获取
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取单个流程", notes = "获取单个流程")
    @PostMapping("/process/getOne")
    public RobotProcessSettingNodeResp getRobotProcessOne(@RequestParam("id") Long id) {
        return robotProcessSettingNodeApi.getRobotProcessOne(id);
    }

    /**
     * 新增流程管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "新增流程管理", notes = "新增流程管理")
    @PostMapping("/process/save")
    public Long saveRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeReq robotProcessSettingNodeReq) {
        return robotProcessSettingNodeApi.saveRobotProcessSetting(robotProcessSettingNodeReq);
    }

    /**
     * 修改流程管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "修改流程管理", notes = "修改流程管理")
    @PostMapping("/process/edit")
    public int updateRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeEditReq robotProcessSettingNodeEditReq) {
        return robotProcessSettingNodeApi.updateRobotProcessSetting(robotProcessSettingNodeEditReq);
    }

    /**
     * 删除流程管理
     *
     * @param
     * @return
     */
    @ApiOperation(value = "删除流程管理", notes = "删除流程管理")
    @PostMapping("/process/delete")
    public int deleteRobotProcessSettingById(@RequestBody Long id) {
        return robotProcessSettingNodeApi.deleteRobotProcessSettingById(id);
    }

    /**
     * 开关控制
     *
     * @param
     * @return
     */
    @ApiOperation(value = "开关控制", notes = "开关控制")
    @PostMapping("/process/open")
    public int updateRobotProcessSettingDerailById(@RequestBody @Valid RobotProcessSettingNodeEditDerailReq robotProcessSettingNodeEditDerailReq) {
        return robotProcessSettingNodeApi.updateRobotProcessSettingDerailById(robotProcessSettingNodeEditDerailReq);
    }

    @Resource
    private RebotSettingApi rebotSettingApi;

    /**
     * 机器人设置保存
     *
     * @param rebotSettingReq
     * @return
     */
    @ApiOperation(value = "机器人设置保存", notes = "机器人设置保存")
    @PostMapping("/setting/save")
    @XssCleanIgnore
    public int saveRebotSettingReq(@RequestBody @Valid RebotSettingReq rebotSettingReq) {
        log.info("rebotSettingReq  RobotSceneNode controller enter" + rebotSettingReq);
        return rebotSettingApi.saveRebotSettingReq(rebotSettingReq);
    }

    /**
     * 机器人设置查询接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "机器人设置查询接口", notes = "机器人设置查询接口")
    @GetMapping("/setting/list")
    @XssCleanIgnore
    public RebotSettingResp getRebotSettingReq() {
        RebotSettingQueryReq rebotSettingQueryReq = new RebotSettingQueryReq();
        return rebotSettingApi.getRebotSettingReq(rebotSettingQueryReq);
    }

    /**
     * 机器人设置查询接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "场景素材关联保存接口", notes = "场景素材关联保存接口")
    @PostMapping("/sceneMaterial/save")
    public void saveSceneMaterial(@RequestBody RobotSceneMaterialReq robotSceneMaterialReq) {
        robotSceneMaterialApi.saveSceneMaterial(robotSceneMaterialReq);
    }

    /**
     * 机器人设置查询接口
     *
     * @param
     * @return
     */
    @ApiOperation(value = "场景素材关联查询接口", notes = "场景素材关联查询接口")
    @PostMapping("/sceneMaterial/list")
    public RobotSceneMaterialResp getSceneMaterial(@RequestBody RobotSceneMaterialReq robotSceneMaterialReq) {
        return robotSceneMaterialApi.getSceneMaterial(robotSceneMaterialReq);
    }

    @ApiOperation(value = "查询机器人基础设置")
    @PostMapping("/setting/getRobotBaseSetting")
    public RobotBaseSettingVo getRobotBaseSetting() {
        return rebotSettingApi.getRobotBaseSetting();
    }

    @ApiOperation(value = "配置机器人基础设置")
    @PostMapping("/setting/configBaseSetting")
    public void configBaseSetting(@RequestBody @Valid RobotBaseSettingVo baseConfig) {
        rebotSettingApi.configBaseSetting(baseConfig);
    }

    @ApiOperation(value = "查询机器人默认回复设置")
    @PostMapping("/setting/getDefaultReplyConfig")
    public RobotDefaultReplySettingVo getDefaultReplyConfig() {
        return rebotSettingApi.getDefaultReplyConfig();
    }

    @ApiOperation(value = "配置机器人默认回复")
    @PostMapping("/setting/configDefaultReply")
    public void configDefaultReply(@RequestBody @Valid RobotDefaultReplySettingVo settingVo) {
        rebotSettingApi.configDefaultReply(settingVo);
    }
}
