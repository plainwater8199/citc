package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 15:06
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_scene_node")
public class RobotSceneNodeDo  extends BaseDo {

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景描述
     */
    private String sceneValue;

    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deletedTime;

    /**
     * 关联账号
     */
    private String accounts;


    private String creatorOld;

    private String updaterOld;
}
