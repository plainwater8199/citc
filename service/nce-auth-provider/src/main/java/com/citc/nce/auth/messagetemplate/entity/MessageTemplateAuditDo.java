package com.citc.nce.auth.messagetemplate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

/**
 * 模板审核记录表
 * @author yy
 * @date 2024-03-12 19:04:36
 */
@Data
@TableName("message_template_audit")
public class MessageTemplateAuditDo extends BaseDo {
    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    Integer operator;
    /**
     * 服务提供商 蜂动 fontdo   csp自有：owner
     */
    @TableField("supplier_tag")
    String supplierTag;
    /**
     * 审核状态:
     * -1待审核;
     * PENDING 1 审核中;
     * SUCCESS 0 审核通过;
     * FAILED  2 审核不通过
     */
    Integer status;
    /**
     * 审核备注
     */
    String remark;
    /**
     * 送审平台返回的id
     */
    @TableField("platform_template_id")
    String platformTemplateId;
    /**
     * 系统模板id
     */
    @TableField("template_id")
    Long templateId;
    /**
     * 模板素材送审时对应的供应商
     */
    @TableField("chatbot_account")
    String chatbotAccount;
    /**
     * 模板在哪里定义的
     * 1  群发计划（模板管理模块）   2  机器人流程
     */
    @TableField(value ="template_source")
    private Integer templateSource;
    /**
     * 如果模板来自机器人流程，则会有流程id
     */
    private Long processId;
    /**
     * 流程描述id
     */
    private Long processDescId;
    /**
     * 0未删除  1已删除
     */
    @TableField(value ="deleted")
    private Integer deleted;
}
