package com.citc.nce.authcenter.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("user_violation")
public class UserViolationDo extends BaseDo<UserViolationDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 平台：1-核能商城，2-硬核桃社区
     */
    private Integer plate;

    /**
     * 违规类型-默认0
     */
    private Integer violationType;
    /**
     * 违规次数
     */
    private Integer violationNum;

    /**
     * 是否删除 默认0 未删除 1 删除
     */
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deletedTime;
}
