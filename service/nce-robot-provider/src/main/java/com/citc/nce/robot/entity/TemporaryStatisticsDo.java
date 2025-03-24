package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @Author: yangchuang
 * @Date: 2022/10/28 17:44
 * @Version: 1.0
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@TableName("temporary_statistics")
public class TemporaryStatisticsDo  extends BaseDo<TemporaryStatisticsDo> {
    private static final long serialVersionUID = 1L;


    /**
     * 场景id
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 流程id
     */
    @TableField(value = "process_id")
    private Long processId;

    /**
     * chatbotid
     */
    @TableField(value = "chatbot_account_id")
    private String chatbotAccountId;

    /**
     * 供应商类型
     */
    @TableField(value = "chatbot_type")
    private Integer chatbotType;

    /**
     * 类型
     */
    @TableField(value = "type")
    private Integer type;
}
