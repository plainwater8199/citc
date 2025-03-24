package com.citc.nce.robot.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.bean
 * @Author: weilanglang
 * @CreateTime: 2022-07-01  15:41
 
 * @Version: 1.0
 */
@Data
public class RobotSceneMaterialBean implements Serializable {
    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 素材id
     */
    private Long materialId;

    /**
     * 1图片2视频3音频4文件
     */
    private int materialType;

}
