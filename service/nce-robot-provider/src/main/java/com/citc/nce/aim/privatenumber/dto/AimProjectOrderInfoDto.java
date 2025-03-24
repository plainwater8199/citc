package com.citc.nce.aim.privatenumber.dto;

import com.citc.nce.aim.entity.AimProjectDo;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@Data
public class AimProjectOrderInfoDto extends AimProjectDo {
    /**
     * 订单id
     */
    private long orderId;
    /**
     * 订单名称
     * 同项目唯一
     */
    private String orderName;
    /**
     * 订单购买量
     */
    private long orderAmount;
    /**
     * 订单消耗量
     * 默认0
     */
    private long orderConsumption;
    /**
     * 订单状态
     * 0:已关闭 1:已启用 2:已完成 3:已停用
     */
    private int orderStatus;

}
