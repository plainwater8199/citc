package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sign_names")
public class SignNamesDo extends BaseDo<SignNamesDo> implements Serializable {

    private static final long serialVersionUID = -2402778143740993613L;

    @TableField(value = "phone")
    private String phone;//手机号

    @TableField(value = "sign_module_id")
    private String signModuleId;// 打卡组件表id

    @TableField(value = "sign_names_id")
    private String signNamesId; // 打卡名单表uuid

    @TableField(value = "sign_count")
    private Long signCount;// 打卡次数

}
