package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sign_record")
public class SignRecordDo extends BaseDo<SignRecordDo> implements Serializable {

    private static final long serialVersionUID = -2402778143740993613L;

    @TableField(value = "phone")
    private String phone;//手机号

    @TableField(value = "chatbot_id")
    private String chatbotId;//chatbot账号

    @TableField(value = "sign_module_id")
    private String signModuleId;// 打卡组件表id

    @TableField(value = "sign_time")
    private Date signTime; // 打卡时间
}
