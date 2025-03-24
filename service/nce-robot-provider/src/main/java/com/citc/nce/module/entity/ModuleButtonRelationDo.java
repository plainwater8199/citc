package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("module_button_relation")
public class ModuleButtonRelationDo implements Serializable {

    private static final long serialVersionUID = -2402778143740993613L;
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "module_id")
    private String moduleId; // 组件表uuid

    @TableField(value = "module_type")
    private Integer moduleType; // 组件类型 11--订阅，12-取消订阅，13-打卡，14-取消打卡

    @TableField(value = "but_uuid")
    private String butUuid;//按钮ID

    private Date createTime;

    private String creator;

    private Integer deleted;

    private Date deleteTime;

}
