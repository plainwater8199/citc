package com.citc.nce.im.materialSquare.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.materialSquare.mapper.MsSummaryLikeRecordMapper;
import com.citc.nce.im.materialSquare.service.*;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 素材广场，发布汇总 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Service
@Slf4j
@AllArgsConstructor
public class MsSummaryLikeRecordServiceImpl extends ServiceImpl<MsSummaryLikeRecordMapper, MsSummaryLikeRecord> implements IMsLikeRecordService {


    @Override
    public void addLikeRecord(Long msId) {
        String userId = SessionContextUtil.getUserId();
        MsSummaryLikeRecord likeRecord =
                getOne(Wrappers.<MsSummaryLikeRecord>lambdaQuery().eq(MsSummaryLikeRecord::getMssId, msId)
                        .eq(MsSummaryLikeRecord::getUserId, userId)
                        .eq(MsSummaryLikeRecord::getDeleted, 0));
        if (likeRecord != null) {
            throw new BizException(GlobalErrorCode.BAD_REQUEST.getMsg());
        }

        MsSummaryLikeRecord msSummaryLikeRecord = new MsSummaryLikeRecord();
        msSummaryLikeRecord.setMssId(msId);
        msSummaryLikeRecord.setUserId(userId);
        msSummaryLikeRecord.setCreator(userId);
        msSummaryLikeRecord.setCreateTime(new Date());
        msSummaryLikeRecord.setUpdater(userId);
        msSummaryLikeRecord.setUpdateTime(new Date());
        msSummaryLikeRecord.setDeleted(0);
        save(msSummaryLikeRecord);
    }

    @Override
    public void cancelLike(Long msId) {
        String userId = SessionContextUtil.getUserId();
        update(Wrappers.<MsSummaryLikeRecord>lambdaUpdate()
                .set(MsSummaryLikeRecord::getDeleted, 1)
                .set(MsSummaryLikeRecord::getUpdateTime, new Date())
                .set(MsSummaryLikeRecord::getUpdater, userId)
                .eq(MsSummaryLikeRecord::getUserId, userId)
                .eq(MsSummaryLikeRecord::getMssId, msId));
    }
}
