package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 16:32
 * @Version: 1.0
 * @Description:
 */
@Data
public class  RobotSceneNodeResp  implements Serializable {
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    private String sceneName;

    /**
     * 场景描述
     */
    @ApiModelProperty("场景描述")
    private String sceneValue;

    /**
     * 流程集合
     */
    @ApiModelProperty("流程数量")
    private int processNum;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 关联账号
     */
    @ApiModelProperty("关联账号名称")
    private String accountNames;

    /**
     * 关联账号
     */
    @ApiModelProperty("关联账号")
    private String accounts;

    /**
     * 关联账号数量
     */
    @ApiModelProperty("关联账号数量")
    private Integer accountNum;

    /**
     * 账号类型
     */
    @ApiModelProperty("账号信息")
    private List<Map<String,String>> accountInfo;
}
