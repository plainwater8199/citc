package com.citc.nce.im.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;

/**
 * <p>
 * 素材广场，发布汇总 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
public interface IMsViewRecordService extends IService<MsSummaryViewRecord> {


    void addViewRecord(Long msId);
}
