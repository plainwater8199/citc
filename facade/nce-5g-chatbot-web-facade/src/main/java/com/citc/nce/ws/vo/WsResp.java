package com.citc.nce.ws.vo;

import com.citc.nce.robot.vo.RobotProcessButtonResp;
import com.citc.nce.ws.enums.RespMsgTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @authoer:ldy
 * @createDate:2022/7/17 12:08
 * @description:
 */
@Data
public class WsResp {

    private String conversationId;
    /**
     * 消息类型
     *
     * @see RespMsgTypeEnum
     */
    private Integer msgType;
    /**
     * 消息内容
     */
    private Object body;

    /**
     * 全局回复按钮集合
     */
    public List<RobotProcessButtonResp> buttonList;
}
