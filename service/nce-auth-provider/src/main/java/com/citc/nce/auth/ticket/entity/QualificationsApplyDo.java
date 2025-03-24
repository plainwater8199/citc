package com.citc.nce.auth.ticket.entity;

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
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ticket_qualifications_apply")
@ApiModel(value = "ticket_qualifications_apply对象", description = "")
public class QualificationsApplyDo extends BaseDo<QualificationsApplyDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资质名称")
    private Integer qualificationName;

    @ApiModelProperty(value = "处理状态")
    private Integer processingState;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "是否删除（1为已删除，0为未删除）")
    private Integer deleted;

    @ApiModelProperty(value = "未删除默认为0，删除为时间戳")
    private Long deletedTime;

    @ApiModelProperty(value = "是否有处理记录")
    private Boolean hasRecords;
}
