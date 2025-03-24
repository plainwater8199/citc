package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 16:57
 * @Version: 1.0
 * @Description:
 */
/*@Data*/
public class RobotProcessButtonResp implements Serializable {


    /**
     * id
     */
    private Long id;
    /**
     * 节点id
     */
    private Long nodeId;

    @ApiModelProperty("选项分组")
    private List<Integer> option;

    /**
     * 0回复按钮1跳转 按钮2打开app3打 电话4发送地址5
     */
    @ApiModelProperty("0回复按钮1跳转 按钮2打开app3打 电话4发送地址5")
    private String buttonType;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty("0回复按钮1跳转 按钮2打开app3打 电话4发送地址5")
    private String type;
    /**
     * 回复内容
     */
    @ApiModelProperty("回复内容")
    private String buttonDetail;

    @ApiModelProperty("按钮id")
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonDetail() {
        return buttonDetail;
    }

    public void setButtonDetail(String buttonDetail) {
        this.buttonDetail = buttonDetail;
    }

    public List<Integer> getOption() {
        return option;
    }

    public void setOption(List<Integer> option) {
        this.option = option;
    }
}
