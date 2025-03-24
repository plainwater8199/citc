package com.citc.nce.auth.readingLetter.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zjy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reading_letter_template_proved")
@Accessors(chain = true)
public class ReadingLetterTemplateProvedDo extends BaseDo<ReadingLetterTemplateProvedDo> {
    private static final long serialVersionUID = 426404964323428723L;

    /**
     * 用户Id
     */
    private String customerId;
    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 平台模板ID
     */
    private String platformTemplateId;
    /**
     * 模板内容
     */
    private String moduleInformation;
    /**
     * 送审时所使用的外部平台账号
     */
    private String auditAccount;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板类型(1 -19分别为图文单卡等)
     */
    private Integer templateType;
    /**
     * 短信类型   1:5G阅信  2:阅信+
     */
    private Integer smsType;
    /**
     * 运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    private Integer operatorCode;
    /**
     * 是否删除 默认0 未删除  1 删除
     */
    private Integer deleted;
    /**
     * 删除时间戳
     */
    private Long deleteTime;
    /**
     * 支持手机类型 英文逗号分隔
     *  HuaWei华为厂商
     * XiaoMi小米厂商
     * OPPO OPPO厂商
     * VIVO VIVO厂商
     * MEIZU魅族厂商
     */
    private String applicableTerminal;
}
