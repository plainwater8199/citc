package com.citc.nce.robot.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SceneMaterialReq {
    /**
     * 流程id
     */
    private Long processId;
    /**
     * 素材id
     */
    private Long materialId;

    /**
     * 素材类型
     */
    private int materialType;

}
