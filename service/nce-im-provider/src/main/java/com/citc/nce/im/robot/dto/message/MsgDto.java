package com.citc.nce.im.robot.dto.message;

import lombok.Data;

@Data
public class MsgDto {

    /**
     * 会话Id  用来判断该上行是否是首次进入此机器人流程
     */
    private String conversationId;

    private String contributionId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 上行消息类型:
     * action:终端点击建议操作后上行的消息   点击调起指定联系人/点击拍摄按钮/点击地址定位/点击发送地址/点击打开app/点击跳转链接/打电话
     * reply:终端点击建议回复后上行的消息，目前就快捷回复按钮的上行消息是该类型   点击回复按钮
     * text:上行文本                                                   终端主动上行文字
     * sharedData:终端共享设备信息
     * file:上行文件
     */
    private String action;

    private String messageData;

    /**
     * 发送消息使用
     */
    private MessageDetail messageDetail;

    /**
     * 消息来源
     * 0：调试窗口 1：网关
     */
    private String messageSource;
    /**
     * 当前用户
     */
    private String create;
    /**
     * chatbot账号
     */
    private String chatbotAccount;
    /**
     * chatbot账号Id
     * 存消息记录、统计使用
     */
    private String chatbotAccountId;
    /**
     * 5G账户类型
     */
    private String accountType;

    /**
     * 消息类型
     * 1文本 2图片 3视频 4音频 5文件 6单卡 7多卡 8位置
     */
    private Integer messageType;

    /**
     * 按钮id 用于聊天记录保存
     * 一个关键字同时触发多个流程后，用户点击按钮确定是哪个流程。这个被点击按钮的Id
     */
    private String butId;

    /**
     * 终端的按钮回调消息 用于聊天记录保存
     * 0: 非按钮
     * 1： 按钮
     */
    private Integer isDeliveryMessage;

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
    private Integer btnType;

    /**
     * 流程id (一个关键字触发多流程时使用)
     */
    private Long processId;

    private String customerId;

    /**
     * 快照uuid
     * 扩展商城，预览点进机器人调试窗口时使用
     */
    private String snapshotUuid;

// !!!!!重要.供应商版本: chatbot服务提供商tag  蜂动: fontdo   自有chatbot:owner
    private String supplierTag;

    // !!!!!重要.运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
    private Integer operator;

    //这是用来给蜂动消息填充${detailId}动态参数的值, 其他渠道用不上
    private Long detailId;

}
