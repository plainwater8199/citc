package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:00
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("form_management")
public class FormManagementDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 表单名称
     */
    @TableField(value = "form_name")
    private String formName;

    /**
     * 表单内容
     */
    @TableField(value = "form_details")
    private String formDetails;

    /**
     * 表单草稿
     */
    @TableField(value = "form_draft")
    private String formDraft;

    /**
     * 2已发布，1待发布，0未发布
     */
    @TableField(value = "form_status")
    private int formStatus;

    /**
     * 表单封面
     */
    @TableField(value = "form_cover")
    private String formCover;

    /**
     * 0未删除  1已删除
     */
    @TableField(value = "deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    private String creatorOld;

    private String updaterOld;

}
