package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-06  17:32
 
 * @Version: 1.0
 */
@Data
public class RobotProcessTreeResp implements Serializable {
    private long processId;//流程ID
    private String processDes;//设计图
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 最近修改
     */
    private Date modifiedTime;

    /**
     * 最新发布
     */
    private Date releaseTime;
    /**
     * 审核结果
     */
    private  String auditResult;
}
