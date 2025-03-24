package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.List;

@Data
public class RobotSceneMaterialReq {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 素材id
     */
    private List<SceneMaterialReq> sceneMaterialReqList;

}
