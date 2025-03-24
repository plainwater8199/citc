package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName("sign_module")
public class SignModuleDo extends BaseDo<SignModuleDo> implements Serializable {

    private static final long serialVersionUID = -2402778143740993613L;

    @TableField(value = "sign_module_id")
    private String signModuleId; // 打卡组件表uuid

    @TableField(value = "name")
    private String name;//名称

    @TableField(value = "description")
    private String description;//描述

    @TableField(value = "join_success")
    private Integer joinSuccess;//参加打卡成功提示(0:默认提示，1:提示)

    @TableField(value = "join_success_5g_tmp_id",updateStrategy = FieldStrategy.ALWAYS)
    private Long joinSuccess5gTmpId;// 参加打卡成功5G消息模板id

    @TableField(value = "sign_success")
    private Integer signSuccess;// 打卡成功提示(0:默认提示，1:提示)

    @TableField(value = "sign_success_5g_tmp_id",updateStrategy = FieldStrategy.ALWAYS)
    private Long signSuccess5gTmpId;//打卡成功5G消息模板id

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "delete_time")
    private Long deleteTime;

    @TableField(value = "sign_time_warn")
    private Integer signTimeWarn;//打卡时间提示

    @TableField(value = "sign_time_5g_tmp_id")
    private Long signTime5gTmpId;//打卡时间提醒5G消息模板id

    @TableField(value = "sign_repeat_warn")
    private Integer signRepeatWarn;//重复打卡提醒提示

    @TableField(value = "sign_repeat_5g_tmp_id")
    private Long signRepeat5gTmpId;//重复打卡提醒5G消息模板id

    @TableField(value = "sign_time_type")
    private String signTimeType; //打卡类型类型(DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天))

    @TableField(value = "sign_start_time")
    private String signStartTime;//打卡开始时间

    @TableField(value = "sign_end_time")
    private String signEndTime;//打卡结束时间
}
