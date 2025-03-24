package com.citc.nce.authcenter.identification.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/07/14
 * @Version 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_certificate_options")
@ApiModel(value = "CertificateOptionsDo对象", description = "用户账号资质信息表")
public class CertificateOptionsDo extends BaseDo<CertificateOptionsDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "资质ID")
    private Integer certificateId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "资质申请状态(1待审核,2审核不通过,3审核通过))")
    private Integer certificateApplyStatus;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "审批通过时间")
    private Date approvalTime;

    @ApiModelProperty(value = "资质状态(0开启,1关闭,2无状态)")
    private Integer certificateStatus;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳")
    private Long deletedTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "业务表id")
    private Long businessId;

}
