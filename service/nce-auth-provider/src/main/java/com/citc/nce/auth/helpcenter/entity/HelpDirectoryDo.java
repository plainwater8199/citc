package com.citc.nce.auth.helpcenter.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yy
 * @date 2024-05-06 11:03:55
 * 帮助中心目录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("help_directory")
public class HelpDirectoryDo extends BaseDo<HelpDirectoryDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目录内容
     */
    String content;
    /**
     * 目录状态 -1 草稿 0 已发布 1 更新已发布
     */
    Integer status;
    /**
     * 最新发布时间
     */
    Date publishTime;
    /**
     * 最新发布者id
     */
    String publisher;
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
