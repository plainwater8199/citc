package com.citc.nce.robot.service;

import com.citc.nce.robot.vo.RebotSettingReq;
import com.citc.nce.robot.vo.RebotSettingResp;
import com.citc.nce.robot.vo.RobotSceneMaterialReq;
import com.citc.nce.robot.vo.RobotSceneMaterialResp;


/**
 * @Author: yangchuang
 * @Date: 2022/7/5 17:10
 * @Version: 1.0
 * @Description:
 */
public interface RobotSceneMaterialService {
    int saveSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq);

    RobotSceneMaterialResp getRobotSceneMaterial(RobotSceneMaterialReq robotSceneMaterialReq);
}
