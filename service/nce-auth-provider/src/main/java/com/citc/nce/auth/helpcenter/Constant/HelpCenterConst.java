package com.citc.nce.auth.helpcenter.Constant;

/**
 * @author yy
 * @date 2024-05-11 11:14:10
 */
public interface HelpCenterConst {
    /**
     * 目录状态  草稿
     */
    Integer status_draft=-1;
    /**
     * 目录状态  已发布
     */
    Integer status_audited=0;
    /**
     * 目录状态 更新已发布
     */
    Integer status_updated=1;

    /**
     * 这条记录的用途 0  用于编辑的记录
     */
    Integer recordType_edit=0;

    /**
     * 这条记录的用途  1 发布后用于查看的记录
     */
    Integer recordType_published=1;
    /**
     * 目录类型  目录
     */
    Integer directory_type_directory=1;
    /**
     * 目录类型  文档
     */
    Integer directory_type_article=2;
}
