package com.citc.nce.im.materialSquare.service;

import com.citc.nce.robot.api.tempStore.domain.GoodsManageLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品操作日志 服务类
 * </p>
 *
 * @author bydud
 * @since 2023-11-27 10:11:42
 */
public interface IGoodsManageLogService extends IService<GoodsManageLog> {

    String TYPE_ACTIVE_OFF = "下架素材";
    String TYPE_PASS = "审核通过";
    String TYPE_FAILED = "审核不通过";
}
