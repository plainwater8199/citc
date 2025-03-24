package com.citc.nce.robot.controller;

import com.citc.nce.robot.RebotSettingApi;
import com.citc.nce.robot.RobotSceneMaterialApi;
import com.citc.nce.robot.service.RobotSceneMaterialService;
import com.citc.nce.robot.service.RobotSettingService;
import com.citc.nce.robot.vo.RebotSettingReq;
import com.citc.nce.robot.vo.RebotSettingResp;
import com.citc.nce.robot.vo.RobotSceneMaterialReq;
import com.citc.nce.robot.vo.RobotSceneMaterialResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 15:01
 * @Version: 1.0
 * @Description:
 */
@RestController()
@Slf4j
public class RobotSceneMaterialController implements RobotSceneMaterialApi {

    @Resource
    private RobotSceneMaterialService robotSceneMaterialService;

    @Override
    public int saveSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq) {
        return robotSceneMaterialService.saveSceneMaterial(robotSceneMaterialReq);
    }

    @Override
    public RobotSceneMaterialResp getSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq) {
        return robotSceneMaterialService.getRobotSceneMaterial(robotSceneMaterialReq);
    }
}
