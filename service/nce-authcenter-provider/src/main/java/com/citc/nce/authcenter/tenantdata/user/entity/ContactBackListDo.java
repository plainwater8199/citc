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
@TableName("contact_blacklist")
public class ContactBackListDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @TableField(value="person_name")
    private String personName;

    /**
     * 手机号
     */
    @TableField(value="phone_num")
    private String phoneNum;

    /**
     * 0未删除  1已删除
     */
    @TableField(value="deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value="delete_time")
    private Date deleteTime;

    private String creatorOld;

    private String updaterOld;

}
