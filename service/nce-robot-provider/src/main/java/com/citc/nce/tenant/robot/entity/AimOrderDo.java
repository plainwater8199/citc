package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@Data
@TableName("aim_order")
public class AimOrderDo extends BaseDo {
    /**
     * 项目编码
     * 用来识别项目，项目唯一
     */
    private String projectId;
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
    /**
     * 是否删除
     * 0:未删除 1:已删除
     */
    private int deleted;

    private String creatorOld;

    private String updaterOld;

}
