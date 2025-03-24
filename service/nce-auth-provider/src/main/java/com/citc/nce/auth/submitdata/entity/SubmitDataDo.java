package com.citc.nce.auth.submitdata.entity;

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
@TableName("submit_data")
public class SubmitDataDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 表单id
     */
    @TableField(value ="form_id")
    private Long formId;

    /**
     * 提交数据
     */
    @TableField(value ="submit_value")
    private String submitValue;

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

}
