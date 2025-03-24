package com.citc.nce.dataStatistics.entity;

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
 * <p>
 * 会话用户统计表
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("conversational_quantity_statistic")
@ApiModel(value = "ConversationalQuantityStatisticDo对象", description = "会话用户统计表")
public class ConversationalQuantityStatisticDo extends BaseDo<ConversationalQuantityStatisticDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "运营商类型")
    private Integer operatorType;

    @ApiModelProperty(value = "发送量")
    private Long sendNum;

    @ApiModelProperty(value = "上行量")
    private Long upsideNum;

    @ApiModelProperty(value = "会话量")
    private Long sessionNum;

    @ApiModelProperty(value = "有效会话量")
    private Long effectiveSessionNum;

    @ApiModelProperty(value = "新增用户数")
    private Long newUsersNum;

    @ApiModelProperty(value = "活跃用户数")
    private Long activeUsersNum;

    @ApiModelProperty(value = "chatbotId")
    private String chatbotId;
    
    @ApiModelProperty(value = "时间(小时)")
    private Date hours;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted = 0;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
