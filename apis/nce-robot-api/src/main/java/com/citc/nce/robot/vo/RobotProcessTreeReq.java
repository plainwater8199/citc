package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsProject: chatbot
 * @BelongsPackage: com.citc.nce.robot.vo
 * @Author: weilanglang
 * @CreateTime: 2022-07-06  17:32
 
 * @Version: 1.0
 */
@Data
public class RobotProcessTreeReq implements Serializable {
    @ApiModelProperty("流程内容ID")
    private long processDescId;//流程ID
    @ApiModelProperty("流程ID")
    private long processId;//流程ID
    @ApiModelProperty("设计图")
    private String processDes;//设计图
    @ApiModelProperty("快捷按钮")
    private String robotShortcutButtons;//快捷按钮
    /**
     * 提问数量
     */
    @ApiModelProperty("提问数量")
    private Integer poseNum;

    /**
     * 分支数量
     */
    @ApiModelProperty("分支数量")
    private Integer branchNum;

    /**
     * 发送消息
     */
    @ApiModelProperty("发送消息")
    private Integer sendNum;

    /**
     * 指令节点
     */
    @ApiModelProperty("指令节点")
    private Integer instructionNode;

    /**
     * 变量操作
     */
    @ApiModelProperty("变量操作")
    private Integer variableOperation;

    /**
     * 子流程
     */
    @ApiModelProperty("子流程")
    private Integer subProcess;

    /**
     * 联系人操作
     */
    @ApiModelProperty("联系人操作")
    private Integer contactAction;
    /**
     * 流程涉及chatbot账号
     */
    @ApiModelProperty("流程涉及chatbot账号")
    private String  accounts;

    private List<Long> templateIds;
    //需要审核的新模板和修改过内容的模板
    int needAuditForUpdatedTempalte;
    @ApiModelProperty("是否检查过蜂动支持按钮   0 未检查  1 已检查")
    int isChecked;

}
