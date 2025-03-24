package com.citc.nce.im.session.processor.bizModel;

import com.citc.nce.robot.common.ResponsePriority;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RebotSettingModel {
    /**
     * 机器人id
     */
    private Long id;
    /**
     * 等待时间
     */
    private Integer waitTime;

    /**
     * 兜底回复类型 0代表发送全部 1代表随机发送一条
     */
    private int lastReplyType;

    /**
     * 兜底回复
     */
    private String lastReply;

    /**
     * !!!!todo 兜底回复模板的id(supplier方式)
     */
    private String templateId;

    /**
     * 全局按钮类型0代表仅在兜底回复显示1代表在机器人所有回复显示
     */
    private int globalType;

    /**
     * 按钮集合
     */
    private List<RobotProcessButtonResp> buttonList;

    /**
     * 回复开关 0:关闭 1:开启
     */
    @ApiModelProperty(value = "回复开关 0:关闭 1:开启")
    private int replySwitch;

    /**
     * 回复类型 0:自定义回复 1:大型回复
     */
    @ApiModelProperty(value = "回复类型 0:自定义回复 1:大型回复")
    private int replyType;
    /**
     * 选择大模型，选项之间以,分隔
     */
    @ApiModelProperty(value = "选择大模型，选项之间以,分隔")
    private String module;
    /**
     * 回复方式 0:流式 1:非流式
     */
    @ApiModelProperty(value = "回复方式 0:流式 1:非流式")
    private Integer replyMethod;
    /**
     * api
     */
    @ApiModelProperty(value = "apiKey")
    private String apiKey;
    /**
     * secret
     */
    @ApiModelProperty(value = "secretKey")
    private String secretKey;
    /**
     * 服务地址
     */
    @ApiModelProperty(value = "服务地址")
    private String serviceAddress;

    @ApiModelProperty("响应优先级")
    private List<ResponsePriority> responsePriorities;
}
