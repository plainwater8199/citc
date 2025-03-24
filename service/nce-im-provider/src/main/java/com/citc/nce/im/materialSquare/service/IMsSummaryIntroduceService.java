package com.citc.nce.im.materialSquare.service;

import com.citc.nce.robot.api.materialSquare.entity.MsSummaryIntroduce;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 素材广场，发布汇总，作品介绍 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
public interface IMsSummaryIntroduceService extends IService<MsSummaryIntroduce> {

    MsSummaryIntroduce getByVersion(Long mssId, Integer introduceVersion);

    Integer saveIntroduce(Long msId, String introduce);

    MsSummaryIntroduce getLastIntroduce(Long msId);
}
