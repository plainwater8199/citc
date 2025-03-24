package com.citc.nce.authcenter.auth.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @authoer:ldy
 * @createDate:2022/7/3 1:48
 * @description:
 */
@Data
@Accessors(chain = true)
public class AdminUserInfoResp {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账户名 collate utf8mb4_0900_ai_ci
     */
    private String accountName;

    /**
     * 用户实名认证真实姓名 collate utf8mb4_0900_ai_ci
     */
    private String fullName;

    /**
     * 用户头像uuid
     */
//    private String userImgUuid;


    /**
     * 手机号 collate utf8mb4_0900_ai_ci
     */
    private String phone;

    /**
     * 电子邮箱 collate utf8mb4_0900_ai_ci
     */
//    private String mail;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人 collate utf8mb4_0900_ai_ci
     */
    private String updater;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 默认0 未删除 1 删除
     */
    private Integer deleted;

    /**
     * 删除时间戳
     */
    private Long deletedTime;
}
