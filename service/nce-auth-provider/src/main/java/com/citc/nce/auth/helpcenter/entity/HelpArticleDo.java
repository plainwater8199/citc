package com.citc.nce.auth.helpcenter.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 * @date 2024-05-06 11:17:01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("help_article")
public class HelpArticleDo  extends BaseDo<HelpArticleDo> implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 文档内容
     */
    String content;
    /**
     * 文档目录id
     */
    String docId;
    /**
     * 文档标题
     */
    String title;
    /**
     * 所属发布版本
     */
    Integer version;
    /**
     * 这条记录的用途 0  用于编辑的记录  1 发布后用于查看的记录
     */
    Integer recordType;
    /**
     * 0未删除  1已删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;
}
