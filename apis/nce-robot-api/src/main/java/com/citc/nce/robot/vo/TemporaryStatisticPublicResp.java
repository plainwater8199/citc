package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class TemporaryStatisticPublicResp implements Serializable {

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 5g账户
     */
    @ApiModelProperty("5g账户")
    private String account;

    /**
     * 通道类型
     */
    @ApiModelProperty("通道类型")
    private int channelType;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Long num;




}
