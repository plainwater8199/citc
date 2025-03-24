package com.citc.nce.im.materialSquare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.im.materialSquare.mapper.MsSummaryLikeRecordMapper;
import com.citc.nce.im.materialSquare.mapper.MsSummaryViewRecordMapper;
import com.citc.nce.im.materialSquare.service.IMsLikeRecordService;
import com.citc.nce.im.materialSquare.service.IMsViewRecordService;
import com.citc.nce.robot.api.materialSquare.emums.MsLikeStatus;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryLikeRecord;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryViewRecord;
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
public class MsSummaryViewRecordServiceImpl extends ServiceImpl<MsSummaryViewRecordMapper, MsSummaryViewRecord> implements IMsViewRecordService {


    @Override
    public void addViewRecord(Long msId) {
        String userId = SessionContextUtil.getUserId();
        MsSummaryViewRecord msSummaryViewRecord=new MsSummaryViewRecord();
        msSummaryViewRecord.setMssId(msId);
        msSummaryViewRecord.setUserId(userId);
        msSummaryViewRecord.setCreator(userId);
        msSummaryViewRecord.setCreateTime(new Date());
        save(msSummaryViewRecord);
    }
}
