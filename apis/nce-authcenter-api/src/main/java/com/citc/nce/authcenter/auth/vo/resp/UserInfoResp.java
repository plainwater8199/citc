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
public class UserInfoResp {

    /**
     * 用户名
     */
    private String name;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String mail;

    /**
     * 邮箱激活状态
     */
    private Integer mailType;

    /**
     * 实名认证状态
     */
    private String authType;

    /**
     * 姓名
     */
    private String fullName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照片
     */
    private String idCardImgOne;

    /**
     * 身份证反面照片
     */
    private String idCardImgTwo;

    /**
     * 实名认证时间
     */
    private Date authTime;

    /**
     * 企业认证状态
     */
    private String enterpriseAuthType;

    /**
     * 企业账户名
     */
    private String enterpriseName;

    /**
     * 违规次数
     */
    private Integer unruleNum;

    /**
     * deleted_time
     */
    private Long deletedTime;

    /**
     * user_id
     */
    private String userId;


    private Integer deleted;
}
