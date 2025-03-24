package com.citc.nce.im.mall.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("mall_template")
public class MallTemplateDo extends BaseDo {
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板uuid
     */
    private String templateId;
    /**
     * 模板uuid
     */
    private String goodsId;
    /**
     * 模板描述
     */
    private String templateDesc;
    /**
     * 素材uuid
     */
    private String snapshotUuid;
    /**
     * 5G消息模板内容
     */
    private String moduleInformation;
    /**【V15 V16】开发 素材广场-模板发布删除功能
     *
     */
    private String shortcutButton;
    /**
     * 模板类型 0:机器人, 1:5G消息
     */
    private Integer templateType;
    /**
     * 消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置
     */
    private Integer messageType;
    /**
     * 商品绑定状态 0:未绑定 1:已绑定
     *
     * @sine v16 弃用
     */
    private Integer status;
    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;
    /**
     * 删除时间
     */
    private long deletedTime;

    private Long mssId;
}
