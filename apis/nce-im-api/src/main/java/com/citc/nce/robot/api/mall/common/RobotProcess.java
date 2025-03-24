package com.citc.nce.robot.api.mall.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:48
 */
@Data
public class RobotProcess {
    private String processId;//流程ID
    private String processDes;//设计图
    private String robotShortcutButtons;//快捷按钮
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 最后更新时间
     */
    private String updateTime;

    /**
     * 最近修改
     */
    private String modifiedTime;

    /**
     * 最新发布
     */
    private String releaseTime;
}
