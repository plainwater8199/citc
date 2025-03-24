package com.citc.nce.im.materialSquare.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.im.materialSquare.mapper.MsSummaryIntroduceMapper;
import com.citc.nce.im.materialSquare.service.IMsSummaryIntroduceService;
import com.citc.nce.robot.api.materialSquare.entity.MsSummaryIntroduce;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 素材广场，发布汇总，作品介绍 服务实现类
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Service
public class MsSummaryIntroduceServiceImpl extends ServiceImpl<MsSummaryIntroduceMapper, MsSummaryIntroduce> implements IMsSummaryIntroduceService {

    @Override
    public MsSummaryIntroduce getByVersion(Long mssId, Integer introduceVersion) {
        return lambdaQuery()
                .eq(MsSummaryIntroduce::getMssId, mssId)
                .eq(MsSummaryIntroduce::getIntroduceVersion, introduceVersion)
                .one();
    }

    @Override
    @Transactional
    public Integer saveIntroduce(Long msId, String introduce) {
        MsSummaryIntroduce last = getLastIntroduce(msId);
        if (Objects.isNull(last)) {
            MsSummaryIntroduce summaryIntroduce = new MsSummaryIntroduce();
            summaryIntroduce.setMssId(msId);
            summaryIntroduce.setIntroduceVersion(1);
            summaryIntroduce.setIntroduce(introduce);
            save(summaryIntroduce);
            return 1;
        }

        if (StrUtil.isBlankIfStr(introduce) || !introduce.equals(last.getIntroduce())) {
            MsSummaryIntroduce summaryIntroduce = new MsSummaryIntroduce();
            summaryIntroduce.setMssId(msId);
            Integer version = last.getIntroduceVersion() + 1;
            summaryIntroduce.setIntroduceVersion(version);
            summaryIntroduce.setIntroduce(introduce);
            save(summaryIntroduce);
            return version;
        }

        return last.getIntroduceVersion();
    }

    @Override
    public MsSummaryIntroduce getLastIntroduce(Long msId) {
        return lambdaQuery()
                .eq(MsSummaryIntroduce::getMssId, msId)
                .orderByDesc(MsSummaryIntroduce::getIntroduceVersion)
                .last("limit 1")
                .one();
    }
}
