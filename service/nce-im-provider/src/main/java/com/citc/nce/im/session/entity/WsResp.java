package com.citc.nce.im.session.entity;

import com.alibaba.fastjson.JSONObject;
import com.citc.nce.robot.vo.RobotProcessButtonResp;
import lombok.Data;

import java.util.List;

@Data
public class WsResp {

    //唯一标识一个聊天会话
    private String  contributionId;

    private String conversationId;
    /**
     * 消息类型
     *
     */
    private Integer msgType;
    /**
     * 消息内容
     */
    private Object body;

    /**
     * 通道类型
     */
    private String channelType;
//    private String channelType ="硬核桃";

    private String phone;

    /**
     * chatbot账号
     */
    private String chatbotAccount;
    /**
     * chatbot账号Id
     * 存消息记录、统计使用
     */
    private String chatbotAccountId;

    private String userId;
    //判断是终端还是调试窗口 0调试窗口 1网关
    private Integer falg;

    //是否是提问环节
    private Boolean question = false;

    //是否是机器人兜底回复
    private Boolean pocketBottom = false;
    /**
     * 全局回复按钮集合
     */
    public List<RobotProcessButtonResp> buttonList;

    private String pythonMsg;
    //owner fontdo
    private String supplierTag;
}
