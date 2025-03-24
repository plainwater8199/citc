package com.citc.nce.robot.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/7/5 16:15
 * @Version: 1.0
 * @Description:
 */
@Data
public class UpMsgReq {
    /**
     * 上行消息类型:
     * action:终端点击建议操作后上行的消息   点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话
     * reply:终端点击建议回复后上行的消息，目前就快捷回复按钮的上行消息是该类型   点击回复按钮
     * text:上行文本                                                   终端主动上行文字
     * sharedData:终端共享设备信息
     * file:上行文件
     */
    private String action;
    private String conversationId;
    private String contributionId;
    private String messageId;
    private String sender;
    /**
     * 发送消息使用
     */
    private Object messageDetail;
    /**
     * 上行消息的具体内容字符串，根据
     * 不同类型进行转化和解析；
     * 其中
     * action，reply,text为纯文本，
     * action，reply为下行消息里中postback的内容，
     * file和sharedData为json转的字符串，根
     * 据各自结构体定义解析，详情请参
     * 考附录
     */
    private String messageData;

    /**
     * 消息类型
     * 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
     */
    private Integer messageType;

    /**
     * 消息来源
     */
    private String messageSource;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * chatbot账号
     */
    private String chatbotAccount;

    /**
     * chatbot账号Id  (uuid)
     * 存消息记录、统计使用
     */
    private String chatbotAccountId;
    /**
     * 按钮id
     */
    private String butId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 创建人 ,Chatbot对应的CustomerId
     */
    private String create;

    /**
     * 5G账户类型
     */
    private String accountType;

    /**
     * 点击按钮类型
     *     REPLY(1, "回复按钮"),
     *     JUMP(2, "跳转按钮"),
     *     OPEN_APP(3, "打开app"),
     *     PHONE_CALL(4, "打电话"),
     *     SEND_ADDRESS(5, "发送地址"),
     *     LOCALIZATION(6, "地址定位"),
     *     CAMERA(7, "拍摄按钮"),
     *     CONTACT(8, "调起联系人"),
     *     CALENDAR(9, "添加日历"),
     *     DEVICE(10, "共享设备信息");
     */
    private Integer btnType = 1;

    //判断是终端还是调试窗口 0调试窗口 1网关
    private Integer falg;

    /**
     * 终端的按钮回调消息
     * 0: 非按钮
     * 1： 按钮
     */
    private Integer isDeliveryMessage;

    private String customerId;
    /**
     * 固定菜单的按钮
     * 0: 不是
     * 1： 是
     */
    private Integer isMenuButton;

    /**
     * 快照uuid
     * 扩展商城，预览点进机器人调试窗口时使用
     */
    private String snapshotUuid;
    private LocalDateTime upTime;
}
