package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:17
 * @Version: 1.0
 * @Description:
 */

@Data
@TableName("message_template")
public class MessageTemplateDo extends BaseDo {

    /**
     * 样式名称
     */
    @TableField(value ="template_name")
    private String templateName;

    /**
     * 消息类型
     */
    @TableField(value ="message_type")
    private int messageType;

    /**
     * 模板类型(普通模板:1,个性模板:2)
     */
    @TableField(value ="template_type")
    private Integer templateType;

    /**
     * 数据json
     */
    @TableField(value ="module_information")
    private String moduleInformation;

    /**
     * 快捷按钮
     */
    @TableField(value="shortcut_button")
    private String shortcutButton;

    /**
     * 0未删除  1已删除
     */
    @TableField(value ="deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value ="delete_time")
    private Date deleteTime;

    private String creatorOld;

    private String updaterOld;

}
