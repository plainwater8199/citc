package com.citc.nce.authcenter.tenantdata.user.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @authoer:ldy
 * @createDate:2022/7/14 22:51
 * @description:
 */

@Data
@TableName("user_person_identification")
@Accessors(chain = true)
public class UserPersonIdentificationDo extends BaseDo<UserPersonIdentificationDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过
     */
    private Integer personAuthStatus;

    /**
     * 姓名
     */
    private String personName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照片
     */
    private String idCardImgFront;

    /**
     * 身份证反面照片
     */
    private String idCardImgBack;

    /**
     * 个人实名认证申请时间
     */
    private Date personAuthTime;

    /**
     * 个人认证审核时间
     */
    private Date personAuthAuditTime;

    /**
     * 最新审核备注
     */
    private String auditRemark;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Long deletedTime;

    /**
     *  protal 平台信息(1核能商城2硬核桃3chatbot)
     * */
    private Integer protal;
}