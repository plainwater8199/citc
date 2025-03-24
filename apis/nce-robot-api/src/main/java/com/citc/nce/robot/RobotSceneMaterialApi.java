package com.citc.nce.robot;

import com.citc.nce.robot.vo.RebotSettingReq;
import com.citc.nce.robot.vo.RebotSettingResp;
import com.citc.nce.robot.vo.RobotSceneMaterialReq;
import com.citc.nce.robot.vo.RobotSceneMaterialResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:06
 * @Version: 1.0
 * @Description:
 */


@FeignClient(value = "rebot-service", contextId = "RebotSceneMaterial", url = "${robot:}")
public interface RobotSceneMaterialApi {
    /**
     * 场景素材关系保存
     *
     * @param robotSceneMaterialReq
     * @return
     */
    @PostMapping("/sceneMaterial/save")
    int saveSceneMaterial(@RequestBody RobotSceneMaterialReq robotSceneMaterialReq);

    /**
     * 场景关联素材查询接口
     *
     * @param robotSceneMaterialReq
     * @return
     */
    @PostMapping("/sceneMaterial/list")
    RobotSceneMaterialResp getSceneMaterial(@RequestBody RobotSceneMaterialReq robotSceneMaterialReq);

}
