package com.citc.nce.auth.messagetemplate.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yangyang
 * chatbot 发送消息时的消息体
 */
@Data
public class SupplierTemplateContentReq implements Serializable {

    /**
     * 消息类型 1 文本  2 图片 3 视频 4 音频  6 单卡 7 多卡 8  位置
     */
    @NotNull
    int messageType;
    /**
     * 纯文本消息内容
     */
    String text;

    /**
     * 资源来源类型，ID，URL
     */
    String srcType;
    /**
     * 文件消息时的缩略图信息ID
     */
    String thumbnailId;
    /**
     * 文件消息时的文件信息ID
     */
    String fileId;
    /**
     * 卡片信息时多媒体内容
     */
    Object media;
    /**
     * 卡片布局信息
     */
    Object layout;
    /**
     * 当时文件信息和文本信息时 suggestions 放在这里
     */
    Object suggestions;
    }
