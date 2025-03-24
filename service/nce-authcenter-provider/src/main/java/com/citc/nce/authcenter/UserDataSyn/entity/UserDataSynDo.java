package com.citc.nce.authcenter.UserDataSyn.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户数据同步信息
 * @author zy.qiu
 * @createdTime 2023/5/16 10:36
 */
@Data
@TableName("user_data_syn")
public class UserDataSynDo extends BaseDo<UserDataSynDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;

    private String detail;

    private int deleted;

    private Date deletedTime;
}
