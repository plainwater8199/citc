package com.citc.nce.im.materialSquare.mapper;

import com.citc.nce.robot.api.tempStore.domain.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 模板商城 订单表 Mapper 接口
 * </p>
 *
 * @author bydud
 * @since 2023-11-17 02:11:15
 */
public interface OrderMapper extends BaseMapper<Order> {

    String getOrderSnapshot(Long orderId);

    String getCardStyleContent(Long orderId);
}
