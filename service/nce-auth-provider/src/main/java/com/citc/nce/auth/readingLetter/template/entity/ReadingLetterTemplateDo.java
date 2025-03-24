package com.citc.nce.auth.readingLetter.template.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author zjy
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("reading_letter_template")
@Accessors(chain = true)
public class ReadingLetterTemplateDo extends BaseDo<ReadingLetterTemplateDo> {
    private static final long serialVersionUID = 4264049361711558723L;

    /**
     * 用户Id
     */
    private String customerId;

    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板内容
     */
    private String moduleInformation;
    /**
     * 模板类型(1 -19分别为图文单卡等)
     */
    private Integer templateType;
    /**
     * 是否删除 默认0 未删除  1 删除
     */
    private Integer deleted;
    /**
     * 删除时间戳
     */
    private Long deleteTime;
    /**
     * 缩略图
     */
    private String thumbnail;

}
