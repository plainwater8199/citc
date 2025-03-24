package com.citc.nce.im.session.processor.bizModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class RobotProcessTriggerModel {

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 触发关键字
     */
    private String keyWord;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 流程名称
     */
    private String processName;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotProcessTriggerModel that = (RobotProcessTriggerModel) o;
        return Objects.equals(processId, that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(processId);
    }
}
