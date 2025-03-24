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
public class TemporaryStatisticSessionResp implements Serializable {


    /**
     * chatbotid
     */
    @ApiModelProperty(value = "chatbotid")
    private String chatbotId;

    /**
     * 供应商类型
     */
    @ApiModelProperty(value = "供应商类型")
    private int chatbotType;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Long num;



    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

}
