package com.citc.nce.auth.formmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/12 15:00
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("form_management")
@Accessors(chain = true)
public class FormManagementDo extends BaseDo<FormManagementDo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
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


    @TableField(exist = false)
    private Long oldId;

}
