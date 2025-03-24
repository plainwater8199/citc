package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 16:57
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotProcessButtonReq implements Serializable {


    /**
     * 0回复按钮1跳转 按钮2打开app3打 电话4发送地址5
     */
    @ApiModelProperty("0回复按钮1跳转 按钮2打开app3打 电话4发送地址5")
    @NotNull(message = "回复按钮不能为空")
    private int buttonType;

    /**
     * 回复内容
     */
    @ApiModelProperty("回复内容")
    @NotBlank(message = "回复内容不能为空")
    private String buttonDetail;

    @ApiModelProperty("按钮id")
    private String uuid;

    @ApiModelProperty("选项分组")
    private List<Integer> option;


}
