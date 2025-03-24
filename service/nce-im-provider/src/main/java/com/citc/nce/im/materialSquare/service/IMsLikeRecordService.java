package com.citc.nce.im.materialSquare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.materialSquare.entity.MsSummarySnapshot;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.vo.summary.*;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperate;
import com.citc.nce.robot.api.tempStore.bean.manage.GoodsOperateBatch;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * <p>
 * 素材广场，发布汇总 服务类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
public interface IMsLikeRecordService extends IService<MsSummaryLikeRecord> {


    void addLikeRecord(Long msId);

    void cancelLike(Long msId);
}
