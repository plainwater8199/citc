package com.citc.nce.authcenter.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:24
 * @Version: 1.0
 * @Description:
 */

/**
 * @author dylicr
 * @description 用户基本信息表
 * @date 2022-07-10
 */
@Data
@Accessors(chain = true)
@TableName("user")
public class UserDo extends BaseDo<UserDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名 collate utf8mb4_0900_ai_ci
     */
    private String name;

    /**
     * 用户图像uuid
     */
    @TableField(value = "user_img_uuid")
    private String userImgUuid;

    /**
     * 手机号 collate utf8mb4_0900_ai_ci
     */
    private String phone;

    /**
     * 电子邮箱 collate utf8mb4_0900_ai_ci
     */
    private String mail;

    /**
     * 邮箱是否被激活 0未激活 1已激活
     */
    @TableField(value = "email_activated")
    private Integer emailActivated;

    /**
     * 用户类型：0 个人用户 1 企业用户
     */
    private Integer userType;

    /**
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer personAuthStatus;

    /**
     * 企业认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer enterpriseAuthStatus;

    /**
     * 最新认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer authStatus;

    /**
     * 违规次数
     */
    private Integer unruleNum;

    /**
     * 服务商LOGO
     */
    private String spLogo;

    /**
     * 服务商电话
     */
    private String spTel;

    /**
     * 服务商邮箱
     */
    private String spEmail;

    /**
     * 是否删除 默认0 未删除 1 删除
     */
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deletedTime;

    @ApiModelProperty("csp小号id")
    @TableField(exist = false)
    private Long ctuId;

    @TableField(value = "temp_store_perm")
    private Integer tempStorePerm;

}