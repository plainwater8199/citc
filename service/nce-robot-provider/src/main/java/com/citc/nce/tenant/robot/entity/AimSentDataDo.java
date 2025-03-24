package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("aim_sent_data")
public class AimSentDataDo extends BaseDo<AimSentDataDo> {
    /**
     * 项目编码
     */
    @ApiModelProperty("项目编码")
    private String projectId;
    /**
     * 订单id
     */
    @ApiModelProperty("订单id")
    private Long orderId;
    /**
     * 客户号码
     */
    @ApiModelProperty("客户号码")
    private String calling;

    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;
    /**
     * 删除时间
     */
    private long deletedTime;

    /**
     * 唯一标识
     */
    private String recordId;
}
