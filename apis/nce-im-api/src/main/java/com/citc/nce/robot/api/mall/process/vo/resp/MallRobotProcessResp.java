package com.citc.nce.robot.api.mall.process.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 10:47
 * @Version: 1.0
 * @Description:
 */
@Data
public class MallRobotProcessResp implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(value = "流程uuid", dataType = "String")
    private String processId;

    /**
     * 关键字json集合
     */
    @ApiModelProperty("关键字json集合")
    private String primaryCodeList;

    /**
     * 正则词
     */
    @ApiModelProperty("正则词")
    private String regularCode;

    /**
     * 流程名称
     */
    @ApiModelProperty("流程名称")
    private String processName;

    /**
     * 流程描述
     */
    @ApiModelProperty("流程描述")
    private String processValue;

    /**
     * 0放开1关闭，默认0
     */
    @ApiModelProperty("0放开1关闭，默认0")
    private int derail;

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
     * 最近修改
     */
    @ApiModelProperty("最近修改")
    private Date modifiedTime;

    /**
     * 最新发布
     */
    @ApiModelProperty("最新发布")
    private Date releaseTime;
}
