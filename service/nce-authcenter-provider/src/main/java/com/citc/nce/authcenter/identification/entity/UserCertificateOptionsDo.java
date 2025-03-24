package com.citc.nce.authcenter.identification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author huangchong
 * @description 用户账号资质信息表
 * @date 2022-07-18
 */
@Data
@TableName("user_certificate_options")
@Accessors(chain = true)
public class UserCertificateOptionsDo extends BaseDo<UserCertificateOptionsDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资质id
     */
    private Integer certificateId;

    /**
     * 用户id collate utf8mb4_0900_ai_ci
     */
    private String userId;

    /**
     * 资质申请状态(1待审核,2审核不通过,3审核通过)
     */
    private Integer certificateApplyStatus;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审批通过时间
     */
    private Date approvalTime;

    /**
     * 资质状态(0开启，1关闭，2无状态)
     */
    private Integer certificateStatus;

    /**
     * 是否删除（1为已删除，0为未删除）
     */
    private Integer deleted;

    /**
     * 未删除默认为0，删除为时间戳
     */
    private Long deletedTime;

    /**
     * 备注
     */
    private String remark;

    @ApiModelProperty(value = "业务表id")
    private Long businessId;

}