package com.citc.nce.im.materialSquare.service;

import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryContent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 素材广场，发布汇总，作品内容快照 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
public interface IMsSummaryContentService extends IService<MsSummaryContent> {

    MsSummaryContent getByVersion(Long mssId, Integer contentVersion);

    MsSummaryContent getLastMsSummary(Long mssId);
    Integer saveContent(MsSummary summary);
}
