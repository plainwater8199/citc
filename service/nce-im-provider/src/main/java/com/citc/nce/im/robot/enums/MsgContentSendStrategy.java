package com.citc.nce.im.robot.enums;

/**
 * 消息节点发送消息策略，决定消息节点使用何种方式发送配置的消息文本
 *
 * @author jcrenc
 * @since 2023/7/14 14:13
 */
public enum MsgContentSendStrategy {
    SEND_ALL/*全部发送*/,

    RANDOM_ONE/*随机发送一条*/;
}
