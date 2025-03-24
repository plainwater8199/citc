package com.citc.nce.developer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("developer_send_statistics")
@Accessors(chain = true)
public class DeveloperSendStatisticsDo extends BaseDo<DeveloperSendStatisticsDo> {
    private static final long serialVersionUID = 4262049321711658723L;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("每日时间")
    private Date time;

    @ApiModelProperty("调用次数")
    private Long callCount = 0L;

    @ApiModelProperty("调用成功次数")
    private Long successCount = 0L;

    @ApiModelProperty("调用失败次数")
    private Long failCount = 0L;

    @ApiModelProperty("csp账号")
    private String cspId;

    @ApiModelProperty("发送成功次数")
    private Long sendSuccessCount = 0L;

    @ApiModelProperty("发送失败次数")
    private Long sendFailCount = 0L;

    @ApiModelProperty("发送未知次数")
    private Long sendUnknownCount = 0L;

    @ApiModelProperty("发送未知次数")
    private Long sendDisplayedCount = 0L;

    @ApiModelProperty("账号类型：1-5G消息、2-视频短信消息、3-短信消息")
    private Integer accountType;

    @ApiModelProperty("账号ID")
    private String accountId;

    @ApiModelProperty("5G消息类型所属应用Id")
    private String applicationUniqueId;
}
