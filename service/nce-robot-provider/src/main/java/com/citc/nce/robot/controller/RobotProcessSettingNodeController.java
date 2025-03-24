package com.citc.nce.robot.controller;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.RobotProcessSettingNodeApi;
import com.citc.nce.robot.service.RobotProcessSettingNodeService;
import com.citc.nce.robot.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 流程管理
 * @Author: yangchuang
 * @Date: 2022/7/8 11:11
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class RobotProcessSettingNodeController implements RobotProcessSettingNodeApi {


    @Resource
    private RobotProcessSettingNodeService robotProcessSettingNodeService;

    @PostMapping("/process/list")
    public PageResultResp getRobotProcessSettings(@RequestBody @Valid RobotProcessSettingNodePageReq robotProcessSettingNodePageReq) {
        return robotProcessSettingNodeService.getRobotProcessSettings(robotProcessSettingNodePageReq);
    }
    @PostMapping("/process/getOne")
    public RobotProcessSettingNodeResp getRobotProcessOne(@RequestParam("id") @NotNull Long id) {
        return robotProcessSettingNodeService.getRobotProcessOne(id);

    }
    @PostMapping("/process/save")
    public Long saveRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeReq robotProcessSettingNodeReq) {
        return robotProcessSettingNodeService.saveRobotProcessSetting(robotProcessSettingNodeReq);
    }

    @PostMapping("/process/edit")
    public int updateRobotProcessSetting(@RequestBody @Valid RobotProcessSettingNodeEditReq robotProcessSettingNodeEditReq) {
        return robotProcessSettingNodeService.updateRobotProcessSetting(robotProcessSettingNodeEditReq);
    }

    @PostMapping("/process/delete")
    public int deleteRobotProcessSettingById(@RequestBody Long id) {
        return robotProcessSettingNodeService.deleteRobotProcessSettingById(id);
    }

    @Override
    public int updateRobotProcessSettingDerailById(@RequestBody @Valid RobotProcessSettingNodeEditDerailReq robotProcessSettingNodeEditDerailReq) {
        return robotProcessSettingNodeService.updateRobotProcessSettingDerailById(robotProcessSettingNodeEditDerailReq);
    }

    /**
     * 获取单个流程管理
     *
     * @param
     * @return
     */
    @PostMapping("/process/getRobotProcessSettingById")
    public RobotProcessSettingNodeResp getRobotProcessSettingById(@RequestBody Long id) {
        return robotProcessSettingNodeService.getRobotProcessSettingById(id);
    }
}
